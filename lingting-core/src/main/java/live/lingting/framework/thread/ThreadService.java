package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-20 13:21
 */
public interface ThreadService {

	ExecutorService executor();

	default boolean isRunning() {
		ExecutorService executor = executor();
		return executor != null && !executor.isShutdown() && !executor.isTerminated();
	}

	default void execute(ThrowableRunnable runnable) {
		execute(null, runnable);
	}

	default void execute(String name, ThrowableRunnable runnable) {
		execute(new KeepRunnable(name) {
			@Override
			protected void process() throws Throwable {
				runnable.run();
			}
		});
	}

	default void execute(KeepRunnable runnable) {
		executor().execute(runnable);
	}

	default <T> CompletableFuture<T> async(Supplier<T> supplier) {
		ExecutorService executor = executor();
		return CompletableFuture.supplyAsync(supplier, executor);
	}

	default <T> Future<T> submit(Callable<T> callable) {
		ExecutorService executor = executor();
		return executor.submit(callable);
	}

}
