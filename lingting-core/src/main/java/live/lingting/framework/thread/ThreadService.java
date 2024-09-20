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

	boolean isRunning();

	void execute(ThrowableRunnable runnable);

	void execute(String name, ThrowableRunnable runnable);

	void execute(KeepRunnable runnable);

	<T> CompletableFuture<T> async(Supplier<T> supplier);

	<T> Future<T> submit(Callable<T> callable);
}
