package live.lingting.framework.function;

import java.time.Duration;

/**
 * @author lingting 2024-01-26 15:34
 */
@FunctionalInterface
public interface InterruptedRunnable extends ThrowingRunnable {

	InterruptedRunnable THREAD_SLEEP = () -> Thread.sleep(50);

	static InterruptedRunnable threadSleep(Duration duration) {
		return () -> Thread.sleep(duration.toMillis());
	}

	@Override
	void run() throws InterruptedException;

}
