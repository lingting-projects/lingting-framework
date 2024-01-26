package live.lingting.framework.retry;

import live.lingting.framework.function.ThrowingSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2023-10-23 19:14
 */
@Slf4j
@RequiredArgsConstructor
public class Retry<T> {

	protected final ThrowingSupplier<T> supplier;

	protected final RetryFunction function;

	protected final List<RetryLog<T>> logs = new ArrayList<>();

	/**
	 * 当前重试次数
	 */
	protected int count = 0;

	public static <T> Retry<T> simple(ThrowingSupplier<T> supplier) {
		return simple(3, Duration.ofMillis(10), supplier);
	}

	public static <T> Retry<T> simple(int maxRetryCount, Duration delay, ThrowingSupplier<T> supplier) {
		return new SimpleRetry<>(maxRetryCount, delay, supplier);
	}

	public T get() throws Exception {
		return value().get();
	}

	public RetryValue<T> value() {
		Exception ex = null;
		while (true) {
			try {
				if (ex != null) {
					logs.add(new RetryLog<>(null, ex));
					boolean allowed = function.allowRetry(count, ex);

					// 不允许重试
					if (!allowed) {
						return new RetryValue<>(null, false, logs);
					}

					// 重试休眠时间获取
					Duration delay = function.getDelay(count, ex);
					// 重试计数
					count++;
					// 休眠
					Thread.sleep(delay.toMillis());
				}

				T t = supplier.get();
				logs.add(new RetryLog<>(t, null));
				// 获取到结果
				return new RetryValue<>(t, true, logs);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				ex = e;
			}
			catch (Exception e) {
				ex = e;
			}
		}
	}

}
