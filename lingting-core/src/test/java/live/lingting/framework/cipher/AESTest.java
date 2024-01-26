package live.lingting.framework.cipher;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lingting 2023-10-13 17:15
 */
class AESTest {

	@SneakyThrows
	@Test
	void defaultTest() {
		String secret = "6A921171B0A28CC2";
		String plaintext = "a123456";
		String ciphertext = "dIW9PQf3/GFbuhhtw252yw==";

		AES aes1 = AES.builder().build();
		String e1 = aes1.encrypt().secret(secret).base64(plaintext);
		Assertions.assertEquals(ciphertext, e1);

		String d1 = aes1.decrypt().secret(secret).base64(ciphertext);
		Assertions.assertEquals(plaintext, d1);

		// java 中 默认AES 使用 ECB + pkcs7
		AES aes2 = AES.builder().ecb().pkcs7().build();
		String e21 = aes2.encrypt().secret(secret).base64(plaintext);
		Assertions.assertEquals(ciphertext, e21);

		String d21 = aes2.decrypt().secret(secret).base64(ciphertext);
		Assertions.assertEquals(plaintext, d21);

		// pkcs7 和 pkcs5 等效
		AES aes3 = AES.builder().ecb().pkcs7().build();
		String e31 = aes3.encrypt().secret(secret).base64(plaintext);
		Assertions.assertEquals(ciphertext, e31);

		String d31 = aes3.decrypt().secret(secret).base64(ciphertext);
		Assertions.assertEquals(plaintext, d31);
	}

	@SneakyThrows
	@Test
	void ecb() {
		String secret = "6A921171B0A28CC2";
		String plaintext = "a123456";

		String ciphertextPkcs5 = "dIW9PQf3/GFbuhhtw252yw==";
		AES aes = AES.builder().ecb().pkcs5().build();
		Assertions.assertEquals(ciphertextPkcs5, aes.encrypt().secret(secret).base64(plaintext));
		Assertions.assertEquals(plaintext, aes.decrypt().secret(secret).base64(ciphertextPkcs5));

		String ciphertextPkcs7 = "dIW9PQf3/GFbuhhtw252yw==";
		aes = AES.builder().ecb().pkcs7().build();
		Assertions.assertEquals(ciphertextPkcs7, aes.encrypt().secret(secret).base64(plaintext));
		Assertions.assertEquals(plaintext, aes.decrypt().secret(secret).base64(ciphertextPkcs7));

		String ciphertextIso10126 = "nKnxu8X+MTPePKWnOffLBQ==";
		aes = AES.builder().ecb().iso10126().build();
		// 填入的是随机字节, 所有每次加密结果都不一样, 只需要校验解密
		Assertions.assertEquals(plaintext, aes.decrypt().secret(secret).base64(ciphertextIso10126));

		// 无填充要求明文长度是16字节的倍数
		String plaintextNo = "1234567890123456";
		String ciphertextNo = "Vk90UnG6Meq3uxQJaWl7EQ==";
		aes = AES.builder().ecb().no().build();
		Assertions.assertEquals(ciphertextNo, aes.encrypt().secret(secret).base64(plaintextNo));
		Assertions.assertEquals(plaintextNo, aes.decrypt().secret(secret).base64(ciphertextNo));
	}

	@Test
	@SneakyThrows
	void cbc() {
		String iv = "9A221171B0A18CC2";
		String secret = "6A921171B0A28CC2";
		String plaintext = "a123456";

		String ciphertextPkcs5 = "vlqZ2ozl5muE1XOj3Srh1g==";
		AES aes = AES.builder().cbc().pkcs5().build();
		Assertions.assertEquals(ciphertextPkcs5, aes.encrypt().secret(secret).iv(iv).base64(plaintext));
		Assertions.assertEquals(plaintext, aes.decrypt().secret(secret).iv(iv).base64(ciphertextPkcs5));

		String ciphertextPkcs7 = "vlqZ2ozl5muE1XOj3Srh1g==";
		aes = AES.builder().cbc().pkcs7().build();
		Assertions.assertEquals(ciphertextPkcs7, aes.encrypt().secret(secret).iv(iv).base64(plaintext));
		Assertions.assertEquals(plaintext, aes.decrypt().secret(secret).iv(iv).base64(ciphertextPkcs7));

		String ciphertextIso10126 = "0jdg0wcVcdAzAqewb5LENA==";
		aes = AES.builder().cbc().iso10126().build();
		Assertions.assertEquals(plaintext, aes.decrypt().secret(secret).iv(iv).base64(ciphertextIso10126));

		// 无填充要求明文长度是16字节的倍数
		String plaintextNo = "1234567890123456";
		String ciphertextNo = "eoTQrLWSu1cYUro/HaHzIw==";
		aes = AES.builder().cbc().no().build();
		Assertions.assertEquals(ciphertextNo, aes.encrypt().secret(secret).iv(iv).base64(plaintextNo));
		Assertions.assertEquals(plaintextNo, aes.decrypt().secret(secret).iv(iv).base64(ciphertextNo));

	}

}
