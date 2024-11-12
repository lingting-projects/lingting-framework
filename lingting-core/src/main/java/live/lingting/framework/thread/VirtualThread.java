package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import live.lingting.framework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-18 19:59
 */
@SuppressWarnings("java:S1845")
public final class VirtualThread {

	static final boolean SUPPORT;

	static final VirtualThread.Impl instance;

	static {
		Method method = ClassUtils.method(Thread.class, "ofVirtual");
		SUPPORT = method != null;
		instance = new VirtualThread.Impl();
	}

	private VirtualThread() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static VirtualThread.Impl instance() {
		return instance;
	}

	public static ExecutorService executor() {
		return instance.executor();
	}

	public static VirtualThread.Impl update(ExecutorService executor) {
		return instance().executor(executor);
	}

	public static boolean isSupport() {
		return SUPPORT;
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

	}

}
