package live.lingting.framework.crypto.cipher;

import live.lingting.framework.util.StringUtils;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author lingting 2024-09-04 11:31
 */
public class AbstractCipherBuilder<B extends AbstractCipherBuilder<B>> {

	/**
	 * 加密方式
	 */
	protected String algorithm;

	/**
	 * 加密模式
	 */
	protected String mode;

	/**
	 * 填充模式
	 */
	protected String padding;

	protected Charset charset = StandardCharsets.UTF_8;

	protected SecretKeySpec secret;

	protected IvParameterSpec iv;

	public String symbol() {
		StringBuilder builder = new StringBuilder(algorithm);
		if (StringUtils.hasText(mode)) {
			builder.append("/").append(mode);
		}
		if (StringUtils.hasText(padding)) {
			builder.append("/").append(padding);
		}
		return builder.toString();
	}

	public String mode() {
		return mode;
	}

	public String padding() {
		return padding;
	}

	public B algorithm(String algorithm) {
		this.algorithm = algorithm;
		return (B) this;
	}

	public B mode(String mode) {
		this.mode = mode;
		return (B) this;
	}

	public B padding(String padding) {
		this.padding = padding;
		return (B) this;
	}

	public B charset(Charset charset) {
		this.charset = charset;
		return (B) this;
	}

	public B secret(SecretKeySpec secret) {
		this.secret = secret;
		return (B) this;
	}

	public B secret(byte[] secret) {
		this.secret = new SecretKeySpec(secret, algorithm);
		return (B) this;
	}

	public B secret(String secret) {
		byte[] bytes = secret.getBytes(charset);
		return secret(bytes);
	}

	public B iv(IvParameterSpec iv) {
		this.iv = iv;
		return (B) this;
	}

	public B iv(byte[] iv) {
		this.iv = new IvParameterSpec(iv);
		return (B) this;
	}

	public B iv(String iv) {
		byte[] bytes = iv.getBytes(charset);
		return iv(bytes);
	}

	public Cipher build() {
		String symbol = symbol();
		return new Cipher(algorithm, symbol, charset, secret, iv);
	}

}
