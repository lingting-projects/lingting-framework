package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
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

	public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

	public static ExecutorService executor() {
		return EXECUTOR;
	}

	/**
	 * 线程池是否运行中
	 */
	public static boolean isRunning() {
		return !EXECUTOR.isShutdown() && !EXECUTOR.isTerminated();
	}

	public static void execute(ThrowableRunnable runnable) {
		execute(null, runnable);
	}

	public static void execute(String name, ThrowableRunnable runnable) {
		execute(new KeepRunnable(name) {
			@Override
			protected void process() throws Throwable {
				runnable.run();
			}
		});
	}

	public static void execute(KeepRunnable runnable) {
		EXECUTOR.execute(runnable);
	}

	public static <T> CompletableFuture<T> async(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, EXECUTOR);
	}

	public static <T> Future<T> submit(Callable<T> callable) {
		return EXECUTOR.submit(callable);
	}

}
