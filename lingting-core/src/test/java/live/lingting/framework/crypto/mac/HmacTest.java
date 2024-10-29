package live.lingting.framework.crypto.mac;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-09-04 14:06
 */
class HmacTest {

	String source = "hello";

	String secret = "secret";

	@SneakyThrows
	@Test
	void sha1() {
		Mac mac = Mac.hmacBuilder().sha1().secret(secret).build();
		assertEquals("URIFXAX5RPhXVe/FzYlw4ZTp9Fs=", mac.calculateBase64(source));
		assertEquals("5112055c05f944f85755efc5cd8970e194e9f45b", mac.calculateHex(source));
	}

	@SneakyThrows
	@Test
	void sha256() {
		Mac mac = Mac.hmacBuilder().sha256().secret(secret).build();
		assertEquals("iKqz7ejTrflNJquQ07r9SiCDBww7zOnAFO4EpEOEfAs=", mac.calculateBase64(source));
		assertEquals("88aab3ede8d3adf94d26ab90d3bafd4a2083070c3bcce9c014ee04a443847c0b", mac.calculateHex(source));
	}

}
