package live.lingting.framework.cipher;

import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author lingting 2023-10-16 14:50
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public abstract class AbstractCrypt<C extends AbstractCrypt<C>> {

	/**
	 * 加密方式, 如 AES
	 */
	protected final String manner;

	/**
	 * 加密具体行为, 如: AES/ECB/NoPadding
	 */
	protected final String symbol;

	protected final Charset charset;

	protected SecretKeySpec secret;

	protected IvParameterSpec iv;

	public Cipher cipherInstance() throws NoSuchPaddingException, NoSuchAlgorithmException {
		return Cipher.getInstance(symbol);
	}

	/**
	 * @param mode {@link Cipher#ENCRYPT_MODE}
	 */
	public Cipher cipher(int mode, SecretKeySpec secret)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		Cipher cipher = cipherInstance();
		cipher.init(mode, secret);
		return cipher;
	}

	/**
	 * @param mode {@link Cipher#ENCRYPT_MODE}
	 */
	public Cipher cipher(int mode, SecretKeySpec secret, IvParameterSpec iv) throws NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
		Cipher cipher = cipherInstance();
		cipher.init(mode, secret, iv);
		return cipher;
	}

	public Cipher cipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		int mode = cipherMode();
		if (iv == null) {
			return cipher(mode, secret);
		}
		return cipher(mode, secret, iv);
	}

	public abstract int cipherMode();

	public C secret(SecretKeySpec secret) {
		this.secret = secret;
		return (C) this;
	}

	public C secret(byte[] bytes) {
		return secret(new SecretKeySpec(bytes, manner));
	}

	public C secret(String secret) {
		return secret(secret.getBytes(charset));
	}

	public C iv(IvParameterSpec iv) {
		this.iv = iv;
		return (C) this;
	}

	public C iv(byte[] bytes) {
		return iv(new IvParameterSpec(bytes));
	}

	public C iv(String iv) {
		return iv(iv.getBytes(charset));
	}

}
