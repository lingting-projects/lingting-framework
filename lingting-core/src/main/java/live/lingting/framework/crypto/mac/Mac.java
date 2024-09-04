package live.lingting.framework.crypto.mac;

import live.lingting.framework.crypto.AbstractCrypt;
import live.lingting.framework.util.StringUtils;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author lingting 2024-09-04 11:52
 */
public class Mac extends AbstractCrypt<Mac> {

	public Mac(String algorithm, Charset charset, SecretKeySpec secret, IvParameterSpec iv) {
		super(algorithm, charset, secret, iv);
	}

	@Override
	protected Mac instance(String algorithm, Charset charset, SecretKeySpec secret, IvParameterSpec iv) {
		return new Mac(algorithm, charset, secret, iv);
	}

	// region builder

	public MacBuilder toBuilder() {
		return new MacBuilder().algorithm(algorithm).charset(charset).secret(secret).iv(iv);
	}

	public static MacBuilder builder() {
		return new MacBuilder();
	}

	public static MacBuilder.Hmac hmacBuilder() {
		return new MacBuilder.Hmac();
	}

	// endregion

	public javax.crypto.Mac instance() throws NoSuchAlgorithmException {
		return javax.crypto.Mac.getInstance(algorithm);
	}

	public javax.crypto.Mac mac()
			throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
		javax.crypto.Mac mac = instance();
		if (iv == null) {
			mac.init(secret);
		}
		else {
			mac.init(secret, iv);
		}
		return mac;
	}

	public byte[] calculate(byte[] bytes)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		javax.crypto.Mac mac = mac();
		return mac.doFinal(bytes);
	}

	public byte[] calculate(String source)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] bytes = source.getBytes(charset);
		return calculate(bytes);
	}

	public String calculateString(byte[] bytes)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] calculate = calculate(bytes);
		return new String(calculate, charset);
	}

	public String calculateString(String source)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] bytes = source.getBytes(charset);
		return calculateString(bytes);
	}

	public String calculateBase64(byte[] bytes)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] calculate = calculate(bytes);
		return StringUtils.base64(calculate);
	}

	public String calculateBase64(String source)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] bytes = source.getBytes(charset);
		return calculateBase64(bytes);
	}

	public String calculateHex(byte[] bytes)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] calculate = calculate(bytes);
		return StringUtils.hex(calculate);
	}

	public String calculateHex(String source)
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException {
		byte[] bytes = source.getBytes(charset);
		return calculateHex(bytes);
	}

}
