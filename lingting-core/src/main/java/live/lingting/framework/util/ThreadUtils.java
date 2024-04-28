package live.lingting.framework.util;

import live.lingting.framework.function.ThrowableRunnable;
import live.lingting.framework.thread.KeepRunnable;
import live.lingting.framework.thread.ThreadPool;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

/**
 * @author lingting 2023-11-15 16:44
 */
@UtilityClass
public class ThreadUtils {

	public static ThreadPool instance() {
		return ThreadPool.instance();
	}

	public static ThreadPoolExecutor executor() {
		return instance().getPool();
	}

	public static void execute(ThrowableRunnable runnable) {
		execute(null, runnable);
	}

	public static void execute(String name, ThrowableRunnable runnable) {
		instance().execute(name, runnable);
	}

	public static void execute(KeepRunnable runnable) {
		instance().execute(runnable);
	}

	public static <T> CompletableFuture<T> async(Supplier<T> supplier) {
		return instance().async(supplier);
	}

	public <T> Future<T> submit(Callable<T> callable) {
		return instance().submit(callable);
	}

}
