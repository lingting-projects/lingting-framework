package live.lingting.framework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author lingting 2023-06-25 16:56
 */
class StringUtilsTest {

	@Test
	void underscoreToHump() {
		String raw = "contact_id";
		String hump = StringUtils.underscoreToHump(raw);
		Assertions.assertEquals("contactId", hump);
	}

	@Test
	void hex() {
		String hex = "9ab8bc5c3e1792047b699f5c487d25f8";
		byte[] bytes = new byte[] { -102, -72, -68, 92, 62, 23, -110, 4, 123, 105, -97, 92, 72, 125, 37, -8 };
		Assertions.assertArrayEquals(bytes, StringUtils.hex(hex));
		Assertions.assertEquals(hex, StringUtils.hex(bytes));
	}

	@Test
	void base64() {
		String source = "Base64原文";
		byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
		String base64 = "QmFzZTY05Y6f5paH";

		Assertions.assertEquals(base64, StringUtils.base64(bytes));
		Assertions.assertEquals(source, new String(StringUtils.base64(base64), StandardCharsets.UTF_8));
	}

}
