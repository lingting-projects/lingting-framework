package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-18 19:59
 */
@UtilityClass
@SuppressWarnings("java:S1845")
public class VirtualThread {

	@Getter
	static final boolean support;

	static final VirtualThread.Impl instance;

	static {
		boolean flag = true;
		try {
			Thread thread = Thread.ofVirtual().name("TestVirtualSupport").start(() -> {
				//
			});
			thread.start();
			thread.join();
		}
		catch (Throwable e) {
			flag = false;
		}
		support = flag;
		instance = new VirtualThread.Impl();
	}

	public static VirtualThread.Impl instance() {
		return instance;
	}

	public static ExecutorService executor() {
		return instance.executor();
	}

	public static VirtualThread.Impl update(ExecutorService executor) {
		return instance().executor(executor);
	}

	/**
	 * 线程池是否运行中
	 */
	public static boolean isRunning() {
		return instance.isRunning();
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

		protected ExecutorService executor;

		public Impl() {
			// 如果不支持虚拟线程则使用线程池
			this.executor = isSupport() ? Executors.newVirtualThreadPerTaskExecutor() : ThreadPool.executor();
		}

		public ExecutorService executor() {
			return executor;
		}

		public VirtualThread.Impl executor(ExecutorService executor) {
			this.executor = executor;
			return this;
		}

		/**
		 * 线程池是否运行中
		 */
		@Override
		public boolean isRunning() {
			return !executor.isShutdown() && !executor.isTerminated();
		}

		@Override
		public void execute(ThrowableRunnable runnable) {
			execute(null, runnable);
		}

		@Override
		public void execute(String name, ThrowableRunnable runnable) {
			execute(new KeepRunnable(name) {
				@Override
				protected void process() throws Throwable {
					runnable.run();
				}
			});
		}

		@Override
		public void execute(KeepRunnable runnable) {
			executor.execute(runnable);
		}

		@Override
		public <T> CompletableFuture<T> async(Supplier<T> supplier) {
			return CompletableFuture.supplyAsync(supplier, executor);
		}

		@Override
		public <T> Future<T> submit(Callable<T> callable) {
			return executor.submit(callable);
		}

	}

}
