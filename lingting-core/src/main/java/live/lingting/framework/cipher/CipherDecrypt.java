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
public class CipherDecrypt extends AbstractCrypt<CipherDecrypt> {

	public CipherDecrypt(String manner, String symbol, Charset charset) {
		super(manner, symbol, charset);
	}

	@Override
	public int cipherMode() {
		return Cipher.DECRYPT_MODE;
	}

	public byte[] decrypt(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = cipher();
		return cipher.doFinal(bytes);
	}

	public byte[] decrypt(String ciphertext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = ciphertext.getBytes(charset);
		return decrypt(bytes);
	}

	String decryptToString(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] decrypt = decrypt(bytes);
		return new String(decrypt, charset);
	}

	public String base64(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return decryptToString(bytes);
	}

	public String base64(String ciphertext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = StringUtils.base64(ciphertext);
		return base64(bytes);
	}

	public String hex(byte[] bytes) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		return decryptToString(bytes);
	}

	public String hex(String ciphertext) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
			IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] bytes = StringUtils.hex(ciphertext);
		return hex(bytes);
	}

}
