package live.lingting.framework.cipher;

import live.lingting.framework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author lingting 2023-10-16 14:45
 */
public class CipherEncrypt extends AbstractCrypt<CipherEncrypt> {

	public CipherEncrypt(String manner, String symbol, Charset charset) {
		super(manner, symbol, charset);
	}

	@Override
	public int cipherMode() {
		return Cipher.ENCRYPT_MODE;
	}

	public byte[] encrypt(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = cipher();
		return cipher.doFinal(bytes);
	}

	public byte[] encrypt(String plaintext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = plaintext.getBytes(charset);
		return encrypt(bytes);
	}

	public String base64(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] encrypt = encrypt(bytes);
		return StringUtils.base64(encrypt);
	}

	public String base64(String plaintext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = plaintext.getBytes(charset);
		return base64(bytes);
	}

	public String hex(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] encrypt = encrypt(bytes);
		return StringUtils.hex(encrypt);
	}

	public String hex(String plaintext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = plaintext.getBytes(charset);
		return hex(bytes);
	}

}
