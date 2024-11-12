package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import org.slf4j.Logger;

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
@SuppressWarnings("java:S6548")
public final class ThreadPool {

	static final ThreadPool.Impl instance = new Impl(newExecutor());
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ThreadPool.class);

	private ThreadPool() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static ThreadPoolExecutor newExecutor() {
		int core = Runtime.getRuntime().availableProcessors() * 10;
		int max = core * 30;
		int queue = max / 2;
		return new ThreadPoolExecutor(
			// 核心线程数大小. 不论是否空闲都存在的线程
			core,
			// 最大线程数
			max,
			// 存活时间. 非核心线程数如果空闲指定时间. 就回收
			// 存活时间不宜过长. 避免任务量遇到尖峰情况时. 大量空闲线程占用资源
			15,
			// 存活时间的单位
			TimeUnit.SECONDS,
			// 等待任务存放队列 - 队列最大值
			// 这样配置. 当积压任务数量为 队列最大值 时. 会创建新线程来执行任务. 直到线程总数达到 最大线程数
			new LinkedBlockingQueue<>(queue),
			// 新线程创建工厂 - LinkedBlockingQueue 不支持线程优先级. 所以直接新增线程就可以了
			runnable -> new Thread(null, runnable),
			// 拒绝策略 - 在主线程继续执行.
			new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public static Impl instance() {
		return instance;
	}

	public static ThreadPoolExecutor executor() {
		return instance.executor();
	}

	public static Impl update(ThreadPoolExecutor executor) {
		return instance().executor(executor);
	}

	/**
	 * 线程池是否运行中
	 */
	public static boolean isRunning() {
		return instance.isRunning();
	}

	/**
	 * 核心线程数
	 */
	public static long getCorePoolSize() {
		return instance.getCorePoolSize();
	}

	/**
	 * 活跃线程数
	 */
	public static long getActiveCount() {
		return instance.getActiveCount();
	}

	/**
	 * 已执行任务总数
	 */
	public static long getTaskCount() {
		return instance.getTaskCount();
	}

	/**
	 * 允许的最大线程数量
	 */
	public static long getMaximumPoolSize() {
		return instance.getMaximumPoolSize();
	}

	/**
	 * 是否可能触发拒绝策略, 仅为估算
	 */
	public static boolean isReject() {
		return instance.isReject();
	}

	public static void execute(ThrowableRunnable runnable) {
		instance.execute(runnable);
	}

	public static void execute(String name, ThrowableRunnable runnable) {
		instance.execute(name, runnable);
	}

	public static void execute(KeepRunnable runnable) {
		instance.execute(runnable);
	}

	public static <T> CompletableFuture<T> async(Supplier<T> supplier) {
		return instance.async(supplier);
	}

	public static <T> Future<T> submit(Callable<T> callable) {
		return instance.submit(callable);
	}

	public static class Impl implements ThreadService {

		protected ThreadPoolExecutor executor;

		public Impl(ThreadPoolExecutor executor) {
			this.executor = executor;
		}

		public ThreadPoolExecutor executor() {
			return executor;
		}

		public Impl executor(ThreadPoolExecutor executor) {
			this.executor = executor;
			return this;
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

	}

}
