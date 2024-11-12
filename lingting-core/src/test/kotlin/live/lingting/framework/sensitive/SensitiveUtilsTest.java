package live.lingting.framework.sensitive;

import org.junit.jupiter.api.Test;

import static live.lingting.framework.sensitive.SensitiveUtils.serialize;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-26 18:01
 */
class SensitiveUtilsTest {

	@Test
	void test() {
		String raw = "这是一个要脱敏的文本";
		String r1 = serialize(raw, 1, 1);
		System.out.println(r1);
		assertEquals("这******本", r1);
		String r2 = serialize(raw, 2, 2);
		System.out.println(r2);
		assertEquals("这是******文本", r2);
		String r12 = serialize(raw, 1, 2);
		System.out.println(r12);
		assertEquals("这******文本", r12);
		String r21 = serialize(raw, 2, 1);
		System.out.println(r21);
		assertEquals("这是******本", r21);
	}

}
