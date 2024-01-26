package live.lingting.framework.retry;

import live.lingting.framework.function.ThrowingSupplier;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lingting 2023-10-24 14:33
 */
class RetryTest {

	@SneakyThrows
	@Test
	void test() {
		int expected = 3;

		AtomicInteger atomic = new AtomicInteger(0);
		ThrowingSupplier<Integer> supplier = () -> {
			int i = atomic.get();
			if (i == expected) {
				return i;
			}
			atomic.set(i + 1);
			throw new IllegalStateException("异常");
		};

		Retry<Integer> retry = Retry.simple(4, Duration.ZERO, supplier);
		RetryValue<Integer> value = retry.value();
		Assertions.assertTrue(value.success());
		Assertions.assertEquals(expected, value.get());
		Assertions.assertEquals(4, value.logs().size());

		atomic.set(0);
		retry = Retry.simple(2, Duration.ZERO, supplier);
		value = retry.value();
		Assertions.assertFalse(value.success());
		Assertions.assertNull(value.value());
		Assertions.assertEquals(3, value.logs().size());

	}

}
