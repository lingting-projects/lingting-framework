package live.lingting.framework.crypto;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;

/**
 * @author lingting 2024-09-04 13:59
 */
public abstract class AbstractCrypt<R extends AbstractCrypt<R>> {

	protected final String algorithm;

	protected final Charset charset;

	protected final SecretKeySpec secret;

	protected final IvParameterSpec iv;

	public AbstractCrypt(String algorithm, Charset charset, SecretKeySpec secret, IvParameterSpec iv) {
		this.algorithm = algorithm;
		this.charset = charset;
		this.secret = secret;
		this.iv = iv;
	}

	public R useSecret(SecretKeySpec secret) {
		return instance(algorithm, charset, secret, iv);
	}

	public R useSecret(byte[] secret) {
		SecretKeySpec spec = new SecretKeySpec(secret, algorithm);
		return useSecret(spec);
	}

	public R useSecret(String secret) {
		byte[] bytes = secret.getBytes(charset);
		return useSecret(bytes);
	}

	public R useIv(IvParameterSpec iv) {
		return instance(algorithm, charset, secret, iv);
	}

	public R useIv(byte[] iv) {
		IvParameterSpec spec = new IvParameterSpec(iv);
		return useIv(spec);
	}

	public R useIv(String iv) {
		byte[] bytes = iv.getBytes(charset);
		return useIv(bytes);
	}

	protected abstract R instance(String algorithm, Charset charset, SecretKeySpec secret, IvParameterSpec iv);

}
