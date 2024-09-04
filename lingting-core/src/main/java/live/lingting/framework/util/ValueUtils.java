package live.lingting.framework.util;

import live.lingting.framework.function.InterruptedRunnable;
import live.lingting.framework.thread.Await;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author lingting 2024-01-26 15:47
 */
@UtilityClass
public class ValueUtils {

	public static void awaitTrue(Supplier<Boolean> supplier) {
		awaitTrue(null, supplier);
	}

	public static void awaitTrue(Duration timeout, Supplier<Boolean> supplier) {
		Await.builder(supplier, Boolean.TRUE::equals).timeout(timeout).await();
	}

	public static void awaitFalse(Supplier<Boolean> supplier) {
		awaitFalse(null, supplier);
	}

	public static void awaitFalse(Duration timeout, Supplier<Boolean> supplier) {
		Await.builder(supplier, Boolean.FALSE::equals).timeout(timeout).await();
	}

	/**
	 * 等待值满足条件, 不满足条件休眠 500 毫秒
	 * @param supplier 值获取
	 * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
	 * @return 值
	 * @param <T> 值类型
	 */
	public static <T> T await(Supplier<T> supplier, Predicate<T> predicate) {
		return await(null, supplier, predicate, InterruptedRunnable.THREAD_SLEEP);
	}

	public static <T> T await(Duration timeout, Supplier<T> supplier, Predicate<T> predicate) {
		return await(timeout, supplier, predicate, InterruptedRunnable.THREAD_SLEEP);
	}

	/**
	 * 等待值满足条件
	 * @param supplier 值获取
	 * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
	 * @param sleep 休眠
	 * @return 值
	 * @param <T> 值类型
	 */
	public static <T> T await(Supplier<T> supplier, Predicate<T> predicate, InterruptedRunnable sleep) {
		return await(null, supplier, predicate, sleep);
	}

	public static <T> T await(Duration timeout, Supplier<T> supplier, Predicate<T> predicate,
			InterruptedRunnable sleep) {
		return Await.builder(supplier, predicate).sleep(sleep).timeout(timeout).await();
	}

	/**
	 * 当前对象是否非null，且不为空
	 * @param value 值
	 * @return boolean 不为空返回true
	 */
	public static boolean isPresent(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof CharSequence sequence) {
			return StringUtils.hasText(sequence);
		}
		if (value instanceof Collection<?> collection) {
			return !collection.isEmpty();
		}
		if (value instanceof Map<?, ?> map) {
			return !map.isEmpty();
		}
		if (value.getClass().isArray()) {
			return Array.getLength(value) > 0;
		}
		return true;
	}

}
