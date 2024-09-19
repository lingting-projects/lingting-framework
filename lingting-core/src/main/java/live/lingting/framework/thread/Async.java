package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import live.lingting.framework.lock.JavaReentrantLock;
import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.util.ValueUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

/**
 * @author lingting 2023-06-05 17:31
 */
@Slf4j
public class Async {

	@Setter
	protected static Executor defaultExecutor = VirtualThread.executor();

	public static final long UNLIMITED = -1;

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	/**
	 * 所有异步任务列表
	 */
	protected final List<StateKeepRunnable> all = new CopyOnWriteArrayList<>();

	/**
	 * 执行中任务列表
	 */
	protected final List<StateKeepRunnable> running = new CopyOnWriteArrayList<>();

	/**
	 * 已完成异步任务列表
	 */
	protected final List<StateKeepRunnable> completed = new CopyOnWriteArrayList<>();

	/**
	 * 待执行任务队列
	 */
	protected final BlockingQueue<StateKeepRunnable> queue = new LinkedBlockingQueue<>();

	/**
	 * 异步任务使用的线程池
	 */
	protected final Executor executor;

	/**
	 * 线程数量限制. -1 表示不限制
	 */
	@Getter
	protected final long limit;

	public Async() {
		this(defaultExecutor);
	}

	public Async(Executor executor) {
		this(executor, UNLIMITED);
	}

	public Async(long limit) {
		this(defaultExecutor, limit);
	}

	public Async(Executor executor, long limit) {
		this.executor = executor;
		this.limit = limit;
	}

	public static Async pool() {
		return pool(UNLIMITED);
	}

	public static Async pool(long limit) {
		Executor e = ThreadUtils.executor();
		return new Async(e, limit);
	}

	public static Async virtual() {
		return virtual(UNLIMITED);
	}

	public static Async virtual(long limit) {
		Executor e = VirtualThread.executor();
		return new Async(e, limit);
	}

	/**
	 * 是否可以无限制使用线程
	 */
	public boolean isUnlimited() {
		return limit == UNLIMITED;
	}

	public void execute(Runnable runnable) {
		execute("", runnable);
	}

	public void execute(String name, Runnable runnable) {
		submit(name, runnable::run);
	}

	public void submit(ThrowableRunnable runnable) {
		submit("", runnable);
	}

	public void submit(String name, ThrowableRunnable runnable) {
		StateKeepRunnable keepRunnable = new StateKeepRunnable(name) {

			@Override
			protected void doProcess() throws Throwable {
				runnable.run();
			}

			@SneakyThrows
			@Override
			protected void onFinally() {
				super.onFinally();
				lock.runByInterruptibly(() -> {
					completed.add(this);
					running.remove(this);
					walk();
				});
			}
		};

		all.add(keepRunnable);
		queue.add(keepRunnable);
		walk();
	}

	/**
	 * 唤醒所有任务. 尝试执行
	 */
	@SneakyThrows
	public void walk() {
		// 上锁确保不会多执行
		lock.runByInterruptibly(() -> {
			// 无限制 || 可以执行新任务
			if (isUnlimited() || running.size() < limit) {
				StateKeepRunnable runnable = queue.poll();
				if (runnable == null) {
					return;
				}
				executor.execute(runnable);
				running.add(runnable);
			}
		});
	}

	public void await() {
		await(null);
	}

	/**
	 * 等待结束, 执行时间超过超时时间的任务强行中断
	 * @param duration 超时时间
	 */
	public void await(Duration duration) {
		await(duration, true);
	}

	/**
	 * 等待结束
	 * @param duration 超时时间
	 * @param forceInterrupt 是否强制中断已超时的任务
	 */
	public void await(Duration duration, boolean forceInterrupt) {
		Supplier<Boolean> supplier = () -> {
			long count = notCompletedCount();
			if (count < 1) {
				return true;
			}

			if (duration == null) {
				return false;
			}
			long millis = duration.toMillis();
			for (StateKeepRunnable runnable : running) {
				// 执行时间超时
				if (runnable.time() >= millis && forceInterrupt) {
					runnable.interrupt();
				}
			}
			return false;
		};
		ValueUtils.awaitTrue(supplier);
	}

	/**
	 * 执行中和待执行的任务数量
	 */
	public long notCompletedCount() {
		return (queue.size() + running.size());
	}

	public long runningCount() {
		return running.size();
	}

	/**
	 * 已完成的数量
	 */
	public long completedCount() {
		return completed.size();
	}

	/**
	 * 所有异步任务数量
	 */
	public long allCount() {
		return all.size();
	}

}
