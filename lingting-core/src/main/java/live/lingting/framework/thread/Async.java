package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowableRunnable;
import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.util.ValueUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lingting 2023-06-05 17:31
 */
@Slf4j
@Getter
public class Async {

	@Setter
	protected static Executor defaultExecutor = ThreadUtils.executor();

	protected final AtomicInteger counter = new AtomicInteger(0);

	@Setter
	protected Executor executor = defaultExecutor;

	public void submit(String name, ThrowableRunnable runnable) {
		increment();
		String threadName = String.format("Async-%s", name);
		KeepRunnable keepRunnable = new KeepRunnable(threadName) {

			@Override
			protected void process() throws Throwable {
				runnable.run();
			}

			@Override
			protected void onFinally() {
				decrement();
			}
		};
		executor.execute(keepRunnable);
	}

	protected void increment() {
		counter.incrementAndGet();
	}

	protected void decrement() {
		counter.decrementAndGet();
	}

	public void await() {
		ValueUtils.awaitTrue(() -> counter.get() < 1);
	}

	public long count() {
		return counter.get();
	}

}
