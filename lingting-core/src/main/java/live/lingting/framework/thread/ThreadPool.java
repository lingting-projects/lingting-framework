package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author lingting 2022/11/17 20:15
 */
@Slf4j
@AllArgsConstructor
@SuppressWarnings("java:S6548")
public class ThreadPool {

	protected static final ThreadPool THREAD_POOL;

	protected static final Integer QUEUE_MAX = 10;

	static {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				// 核心线程数大小. 不论是否空闲都存在的线程
				300,
				// 最大线程数 - 1万个
				10000,
				// 存活时间. 非核心线程数如果空闲指定时间. 就回收
				// 存活时间不宜过长. 避免任务量遇到尖峰情况时. 大量空闲线程占用资源
				10,
				// 存活时间的单位
				TimeUnit.SECONDS,
				// 等待任务存放队列 - 队列最大值
				// 这样配置. 当积压任务数量为 队列最大值 时. 会创建新线程来执行任务. 直到线程总数达到 最大线程数
				new LinkedBlockingQueue<>(QUEUE_MAX),
				// 新线程创建工厂 - LinkedBlockingQueue 不支持线程优先级. 所以直接新增线程就可以了
				runnable -> new Thread(null, runnable),
				// 拒绝策略 - 在主线程继续执行.
				new ThreadPoolExecutor.CallerRunsPolicy());
		THREAD_POOL = new ThreadPool(executor);
	}

	protected ThreadPoolExecutor executor;

	public static ThreadPool instance() {
		return THREAD_POOL;
	}

	public static ThreadPool update(ThreadPoolExecutor executor) {
		return instance().executor(executor);
	}

	public ThreadPoolExecutor executor() {
		return executor;
	}

	public ThreadPool executor(ThreadPoolExecutor executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * 线程池是否运行中
	 */
	public boolean isRunning() {
		return !executor.isShutdown() && !executor.isTerminated();
	}

	/**
	 * 核心线程数
	 */
	public long getCorePoolSize() {
		return executor.getCorePoolSize();
	}

	/**
	 * 活跃线程数
	 */
	public long getActiveCount() {
		return executor.getActiveCount();
	}

	/**
	 * 已执行任务总数
	 */
	public long getTaskCount() {
		return executor.getTaskCount();
	}

	/**
	 * 允许的最大线程数量
	 */
	public long getMaximumPoolSize() {
		return executor.getMaximumPoolSize();
	}

	/**
	 * 是否可能触发拒绝策略, 仅为估算
	 */
	public boolean isReject() {
		long activeCount = getActiveCount();
		long size = getMaximumPoolSize();

		// 活跃线程占比未达到 90% 不可能
		long per = activeCount / size;
		if (per <= 90) {
			return false;
		}

		// 占比达到90%的情况下, 剩余可用线程数小于10 则可能触发拒绝
		return size - activeCount < 10;
	}

	public void execute(ThrowableRunnable runnable) {
		execute(null, runnable);
	}

	public void execute(String name, ThrowableRunnable runnable) {
		execute(new KeepRunnable(name) {
			@Override
			protected void process() throws Throwable {
				runnable.run();
			}
		});
	}

	public void execute(KeepRunnable runnable) {
		executor.execute(runnable);
	}

	public <T> CompletableFuture<T> async(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, executor);
	}

	public <T> Future<T> submit(Callable<T> callable) {
		return executor.submit(callable);
	}

}
