package live.lingting.framework.retry;

import live.lingting.framework.function.ThrowingSupplier;

import java.time.Duration;

/**
 * @author lingting 2023-12-19 13:47
 */
public class SimpleRetry<T> extends Retry<T> {

	/**
	 * 最大重试次数
	 */
	protected final int maxRetryCount;

	/**
	 * 重试延迟
	 */
	protected final Duration delay;

	public SimpleRetry(int maxRetryCount, Duration delay, ThrowingSupplier<T> supplier) {
		super(supplier, new SimpleRetryFunction(maxRetryCount, delay));
		this.maxRetryCount = maxRetryCount;
		this.delay = delay;
	}

	public int getMaxRetryCount() {return this.maxRetryCount;}

	public Duration getDelay() {return this.delay;}

	public static class SimpleRetryFunction implements RetryFunction {

		/**
		 * 最大重试次数
		 */
		protected final int maxRetryCount;

		/**
		 * 重试延迟
		 */
		protected final Duration delay;

		public SimpleRetryFunction(int maxRetryCount, Duration delay) {
			this.maxRetryCount = maxRetryCount;
			this.delay = delay;
		}

		@Override
		public boolean allowRetry(int retryCount, Exception e) {
			return retryCount < maxRetryCount && !(e instanceof InterruptedException);
		}

		@Override
		public Duration getDelay(int retryCount, Exception e) {
			return delay;
		}

	}

}
