package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.util.ValueUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author lingting 2023-06-05 17:31
 */
@Slf4j
public class Async {

	@Setter
	protected static Executor defaultExecutor = ThreadUtils.executor();

	protected final List<StateKeepRunnable> list = new CopyOnWriteArrayList<>();

	protected final Executor executor;

	public Async() {
		this(defaultExecutor);
	}

	public Async(Executor executor) {
		this.executor = executor;
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
		};
		submit(keepRunnable);
	}

	public void execute(Runnable runnable) {
		execute("", runnable);
	}

	public void execute(String name, Runnable runnable) {
		StateKeepRunnable keepRunnable = new StateKeepRunnable(name) {

			@Override
			protected void doProcess() throws Throwable {
				runnable.run();
			}
		};
		submit(keepRunnable);
	}

	protected void submit(StateKeepRunnable runnable) {
		list.add(runnable);
		executor.execute(runnable);
	}

	public void await() {
		await(null);
	}

	/**
	 * 等待结束
	 * @param duration 超时时间
	 */
	public void await(Duration duration) {
		Supplier<Boolean> supplier = () -> {
			long count = count();
			if (count < 1) {
				return true;
			}

			if (duration == null) {
				return false;
			}
			long millis = duration.toMillis();
			for (StateKeepRunnable runnable : list) {
				// 执行时间超时
				if (runnable.time() >= millis) {
					runnable.stop();
				}
			}
			return count() < 1;
		};
		ValueUtils.awaitTrue(supplier);
	}

	public long count() {
		list.removeIf(StateKeepRunnable::isFinish);
		return list.size();
	}

}
