package live.lingting.framework.crypto.cipher;

import live.lingting.framework.crypto.AbstractCryptoBuilder;
import live.lingting.framework.util.StringUtils;

/**
 * @author lingting 2024-09-04 11:31
 */
public class AbstractCipherBuilder<B extends AbstractCipherBuilder<B>> extends AbstractCryptoBuilder<B, Cipher> {

	/**
	 * 加密模式
	 */
	protected String mode;

	/**
	 * 填充模式
	 */
	protected String padding;

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

	public B mode(String mode) {
		this.mode = mode;
		return (B) this;
	}

	public B padding(String padding) {
		this.padding = padding;
		return (B) this;
	}

	@Override
	protected Cipher doBuild() {
		String symbol = symbol();
		return new Cipher(algorithm, mode, padding, symbol, charset, secret, iv);
	}

}
