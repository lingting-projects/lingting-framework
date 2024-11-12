package live.lingting.framework.thread;

import live.lingting.framework.function.InterruptedRunnable;
import live.lingting.framework.util.ThreadUtils;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author lingting 2024-05-31 11:14
 */
public class Await<S> {

	private final Supplier<S> supplier;

	private final Predicate<S> predicate;

	private final InterruptedRunnable sleep;

	private final Duration timeout;

	private final ExecutorService executor;

	public Await(Supplier<S> supplier, Predicate<S> predicate, InterruptedRunnable sleep, Duration timeout, ExecutorService executor) {
		this.supplier = supplier;
		this.predicate = predicate;
		this.sleep = sleep;
		this.timeout = timeout;
		this.executor = executor;
	}


	public S await() {
		Supplier<S> supply = new Supplier<>() {

			@Override
			public S get() {
				while (true) {
					S s = supplier.get();
					if (predicate.test(s)) {
						return s;
					}

					sleep.run();
				}
			}
		};
		// 未设置超时
		if (timeout == null || timeout.isNegative() || timeout.isZero()) {
			return supply.get();
		}

		try {
			// 设置超时
			CompletableFuture<S> future = CompletableFuture.supplyAsync(supply, executor)
				.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
			return future.get();
		}
		catch (ExecutionException e) {
			throw e.getCause();
		}
	}

	public static <S> AwaitBuilder<S> builder(Supplier<S> supplier, Predicate<S> predicate) {
		return Await.<S>builder().supplier(supplier).predicate(predicate);
	}

	public static <S> AwaitBuilder<S> builder() {
		return new AwaitBuilder<>();
	}

	public static class AwaitBuilder<S> {

		private Supplier<S> supplier;

		private Predicate<S> predicate;

		private InterruptedRunnable sleep = InterruptedRunnable.THREAD_SLEEP;

		private Duration timeout;

		private ExecutorService executor = VirtualThread.executor();

		public AwaitBuilder<S> supplier(Supplier<S> supplier) {
			this.supplier = supplier;
			return this;
		}

		public AwaitBuilder<S> predicate(Predicate<S> predicate) {
			this.predicate = predicate;
			return this;
		}

		public AwaitBuilder<S> sleep(InterruptedRunnable sleep) {
			this.sleep = sleep;
			return this;
		}

		public AwaitBuilder<S> timeout(Duration timeout) {
			this.timeout = timeout;
			return this;
		}

		public AwaitBuilder<S> executor(ExecutorService executor) {
			this.executor = executor;
			return this;
		}

		public AwaitBuilder<S> useThreadPool() {
			return executor(ThreadUtils.executor());
		}

		public AwaitBuilder<S> useThreadVirtual() {
			return executor(VirtualThread.executor());
		}

		public Await<S> build() {
			return new Await<>(supplier, predicate, sleep, timeout, executor);
		}

		public S await() {
			return build().await();
		}

	}

}
