package live.lingting.framework.crypto.cipher;

import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author lingting 2024-09-04 10:17
 */
@Getter
@RequiredArgsConstructor
public class Cipher {

	/**
	 * 加密方式, 如 AES
	 */
	protected final String algorithm;

	/**
	 * 加密模式
	 */
	protected final String mode;

	/**
	 * 填充模式
	 */
	protected final String padding;

	/**
	 * 加密具体行为, 如: AES/ECB/NoPadding
	 */
	protected final String symbol;

	protected final Charset charset;

	protected final SecretKeySpec secret;

	protected final IvParameterSpec iv;

	public Cipher useSecret(SecretKeySpec secret) {
		return new Cipher(algorithm, mode, padding, symbol, charset, secret, iv);
	}

	public Cipher useIv(IvParameterSpec iv) {
		return new Cipher(algorithm, mode, padding, symbol, charset, secret, iv);
	}

	public javax.crypto.Cipher instance() throws NoSuchPaddingException, NoSuchAlgorithmException {
		return javax.crypto.Cipher.getInstance(symbol);
	}

	/**
	 * @param mode {@link javax.crypto.Cipher#ENCRYPT_MODE}
	 */
	public javax.crypto.Cipher cipher(int mode) throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException {
		javax.crypto.Cipher cipher = instance();
		if (iv == null) {
			cipher.init(mode, secret);
		}
		else {
			cipher.init(mode, secret, iv);
		}
		return cipher;
	}

	// region builder

	public CipherBuilder toBuilder() {
		return builder().algorithm(algorithm).mode(mode).padding(padding).charset(charset).secret(secret).iv(iv);
	}

	public static CipherBuilder builder() {
		return new CipherBuilder();
	}

	public static CipherBuilder.AES aesBuilder() {
		return new CipherBuilder.AES();
	}

	// endregion

	// region short

	public static Cipher aes() {
		return aesBuilder().build();
	}

	// endregion

	// region encrypt

	public byte[] encrypt(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		javax.crypto.Cipher cipher = cipher(javax.crypto.Cipher.ENCRYPT_MODE);
		return cipher.doFinal(bytes);
	}

	public byte[] encrypt(String plaintext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = plaintext.getBytes(charset);
		return encrypt(bytes);
	}

	public String encryptBase64(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] encrypt = encrypt(bytes);
		return StringUtils.base64(encrypt);
	}

	public String encryptBase64(String plaintext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = plaintext.getBytes(charset);
		return encryptBase64(bytes);
	}

	public String encryptHex(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] encrypt = encrypt(bytes);
		return StringUtils.hex(encrypt);
	}

	public String encryptHex(String plaintext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = plaintext.getBytes(charset);
		return encryptHex(bytes);
	}

	// endregion
	// region decrypt
	public byte[] decrypt(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		javax.crypto.Cipher cipher = cipher(javax.crypto.Cipher.DECRYPT_MODE);
		return cipher.doFinal(bytes);
	}

	public byte[] decrypt(String ciphertext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = ciphertext.getBytes(charset);
		return decrypt(bytes);
	}

	public String decryptString(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] decrypt = decrypt(bytes);
		return new String(decrypt, charset);
	}

	public String decryptBase64(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return decryptString(bytes);
	}

	public String decryptBase64(String ciphertext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = StringUtils.base64(ciphertext);
		return decryptBase64(bytes);
	}

	public String decryptHex(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return decryptString(bytes);
	}

	public String decryptHex(String ciphertext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = StringUtils.hex(ciphertext);
		return decryptHex(bytes);
	}
	// endregion

}
