package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-09-05 19:48
 */
class DigestUtilsTest {

	@Test
	void md5Hex() throws NoSuchAlgorithmException, IOException {
		String source = "hello md5, digest to hex.";
		byte[] bytes = source.getBytes();
		String hex = "6ac2b48dd5c8584e0882aea82196f5df";

		assertEquals(hex, DigestUtils.md5Hex(source));
		assertEquals(hex, DigestUtils.md5Hex(bytes));
		assertEquals(hex, DigestUtils.md5Hex(new ByteArrayInputStream(bytes)));
		assertEquals(hex, DigestUtils.md5Hex(new ByteArrayInputStream(bytes), 1));
	}

}
