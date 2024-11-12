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

	@Test
	void sha1Hex() throws NoSuchAlgorithmException, IOException {
		String source = "hello sha1, digest to hex.";
		byte[] bytes = source.getBytes();
		String hex = "1bc582ed92e12f1f16c59118fe6d4f4122db7d61";

		assertEquals(hex, DigestUtils.sha1Hex(source));
		assertEquals(hex, DigestUtils.sha1Hex(bytes));
		assertEquals(hex, DigestUtils.sha1Hex(new ByteArrayInputStream(bytes)));
		assertEquals(hex, DigestUtils.sha1Hex(new ByteArrayInputStream(bytes), 1));
	}

	@Test
	void sha256Hex() throws NoSuchAlgorithmException, IOException {
		String source = "hello sha256, digest to hex.";
		byte[] bytes = source.getBytes();
		String hex = "2723b92c190589606102a284b82f2715b0314a27fc5f9fd5865d66da51a8ac52";

		assertEquals(hex, DigestUtils.sha256Hex(source));
		assertEquals(hex, DigestUtils.sha256Hex(bytes));
		assertEquals(hex, DigestUtils.sha256Hex(new ByteArrayInputStream(bytes)));
		assertEquals(hex, DigestUtils.sha256Hex(new ByteArrayInputStream(bytes), 1));
	}

}
