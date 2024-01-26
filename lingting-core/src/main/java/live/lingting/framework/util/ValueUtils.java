package live.lingting.framework.util;

import live.lingting.framework.function.InterruptedRunnable;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
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
		await(supplier, Boolean.TRUE::equals, () -> Thread.sleep(500));
	}

	public static void awaitFalse(Supplier<Boolean> supplier) {
		await(supplier, Boolean.FALSE::equals, () -> Thread.sleep(500));
	}

	/**
	 * 等待值满足条件, 不满足条件休眠 500 毫秒
	 * @param supplier 值获取
	 * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
	 * @return 值
	 * @param <T> 值类型
	 */
	public static <T> T await(Supplier<T> supplier, Predicate<T> predicate) {
		return await(supplier, predicate, () -> Thread.sleep(500));
	}

	/**
	 * 等待值满足条件
	 * @param supplier 值获取
	 * @param predicate 值条件测试, 返回true表示该值为目标值, 当前函数会返回该值
	 * @param sleep 休眠
	 * @return 值
	 * @param <T> 值类型
	 */
	@SneakyThrows
	public static <T> T await(Supplier<T> supplier, Predicate<T> predicate, InterruptedRunnable sleep) {
		while (true) {
			T t = supplier.get();
			if (predicate.test(t)) {
				return t;
			}
			sleep.run();
		}
	}

	/**
	 * 判断对象是否存在值
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
