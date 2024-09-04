package live.lingting.framework.crypto;

import live.lingting.framework.util.StringUtils;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author lingting 2024-09-04 13:47
 */
public abstract class AbstractCryptoBuilder<B extends AbstractCryptoBuilder<B, R>, R> {

	/**
	 * 加密方式
	 */
	protected String algorithm;

	protected Charset charset = StandardCharsets.UTF_8;

	protected SecretKeySpec secret;

	protected IvParameterSpec iv;

	public B algorithm(String algorithm) {
		this.algorithm = algorithm;
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

	public R build() {
		if (!StringUtils.hasText(algorithm)) {
			throw new IllegalArgumentException("algorithm is required");
		}
		if (secret == null) {
			throw new IllegalArgumentException("secret is required");
		}

		return doBuild();
	}

	protected abstract R doBuild();

}
