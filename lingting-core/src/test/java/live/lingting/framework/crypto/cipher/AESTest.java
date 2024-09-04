package live.lingting.framework.crypto.cipher;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lingting 2024-09-04 11:36
 */
class AESTest {

	@SneakyThrows
	@Test
	void defaultTest() {
		String secret = "6A921171B0A28CC2";
		String plaintext = "a123456";
		String ciphertext = "dIW9PQf3/GFbuhhtw252yw==";

		Cipher aes1 = Cipher.aesBuilder().secret(secret).build();
		String e1 = aes1.encryptBase64(plaintext);
		Assertions.assertEquals(ciphertext, e1);

		String d1 = aes1.decryptBase64(ciphertext);
		Assertions.assertEquals(plaintext, d1);

		// java 中 默认Cipher 使用 ECB + pkcs7
		Cipher aes2 = Cipher.aesBuilder().secret(secret).ecb().pkcs7().build();
		String e21 = aes2.encryptBase64(plaintext);
		Assertions.assertEquals(ciphertext, e21);

		String d21 = aes2.decryptBase64(ciphertext);
		Assertions.assertEquals(plaintext, d21);

		// pkcs7 和 pkcs5 等效
		Cipher aes3 = Cipher.aesBuilder().secret(secret).ecb().pkcs7().build();
		String e31 = aes3.encryptBase64(plaintext);
		Assertions.assertEquals(ciphertext, e31);

		String d31 = aes3.decryptBase64(ciphertext);
		Assertions.assertEquals(plaintext, d31);
	}

	@SneakyThrows
	@Test
	void ecb() {
		String secret = "6A921171B0A28CC2";
		String plaintext = "a123456";

		String ciphertextPkcs5 = "dIW9PQf3/GFbuhhtw252yw==";
		Cipher aes = Cipher.aesBuilder().ecb().pkcs5().secret(secret).build();
		Assertions.assertEquals(ciphertextPkcs5, aes.encryptBase64(plaintext));
		Assertions.assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs5));

		String ciphertextPkcs7 = "dIW9PQf3/GFbuhhtw252yw==";
		aes = Cipher.aesBuilder().ecb().pkcs7().secret(secret).build();
		Assertions.assertEquals(ciphertextPkcs7, aes.encryptBase64(plaintext));
		Assertions.assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs7));

		String ciphertextIso10126 = "nKnxu8X+MTPePKWnOffLBQ==";
		aes = Cipher.aesBuilder().ecb().iso10126().secret(secret).build();
		// 填入的是随机字节, 所有每次加密结果都不一样, 只需要校验解密
		Assertions.assertEquals(plaintext, aes.decryptBase64(ciphertextIso10126));

		// 无填充要求明文长度是16字节的倍数
		String plaintextNo = "1234567890123456";
		String ciphertextNo = "Vk90UnG6Meq3uxQJaWl7EQ==";
		aes = Cipher.aesBuilder().ecb().no().secret(secret).build();
		Assertions.assertEquals(ciphertextNo, aes.encryptBase64(plaintextNo));
		Assertions.assertEquals(plaintextNo, aes.decryptBase64(ciphertextNo));
	}

	@Test
	@SneakyThrows
	void cbc() {
		String iv = "9A221171B0A18CC2";
		String secret = "6A921171B0A28CC2";
		String plaintext = "a123456";

		String ciphertextPkcs5 = "vlqZ2ozl5muE1XOj3Srh1g==";
		Cipher aes = Cipher.aesBuilder().cbc().pkcs5().secret(secret).iv(iv).build();
		Assertions.assertEquals(ciphertextPkcs5, aes.encryptBase64(plaintext));
		Assertions.assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs5));

		String ciphertextPkcs7 = "vlqZ2ozl5muE1XOj3Srh1g==";
		aes = Cipher.aesBuilder().cbc().pkcs7().secret(secret).iv(iv).build();
		Assertions.assertEquals(ciphertextPkcs7, aes.encryptBase64(plaintext));
		Assertions.assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs7));

		String ciphertextIso10126 = "0jdg0wcVcdAzAqewb5LENA==";
		aes = Cipher.aesBuilder().cbc().iso10126().secret(secret).iv(iv).build();
		Assertions.assertEquals(plaintext, aes.decryptBase64(ciphertextIso10126));

		// 无填充要求明文长度是16字节的倍数
		String plaintextNo = "1234567890123456";
		String ciphertextNo = "eoTQrLWSu1cYUro/HaHzIw==";
		aes = Cipher.aesBuilder().cbc().no().secret(secret).iv(iv).build();
		Assertions.assertEquals(ciphertextNo, aes.encryptBase64(plaintextNo));
		Assertions.assertEquals(plaintextNo, aes.decryptBase64(ciphertextNo));

	}

}
