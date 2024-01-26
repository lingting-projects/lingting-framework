package live.lingting.framework.thread;

import live.lingting.framework.function.ThrowingRunnable;
import live.lingting.framework.value.WaitValue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2023-06-05 17:31
 */
@Slf4j
@Getter
public class Async {

	@Setter
	protected static ThreadPool defaultPool = ThreadPool.instance();

	protected final WaitValue<Integer> counter = WaitValue.of(0);

	@Setter
	protected ThreadPool pool = defaultPool;

	public void submit(String name, ThrowingRunnable runnable) throws InterruptedException {
		increment();
		pool.execute(String.format("Async-%s", name), () -> {
			try {
				runnable.run();
			}
			finally {
				decrement();
			}
		});
	}

	protected void increment() throws InterruptedException {
		counter.update(v -> v + 1);
	}

	protected void decrement() throws InterruptedException {
		counter.update(v -> v - 1);
	}

	public void await() throws InterruptedException {
		counter.wait(v -> v < 1);
	}

	public long count() {
		return counter.getValue();
	}

}
