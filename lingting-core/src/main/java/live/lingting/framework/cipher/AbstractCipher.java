package live.lingting.framework.cipher;

import live.lingting.framework.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author lingting 2023-10-13 17:04
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractCipher {

	protected final String manner;

	protected final String symbol;

	@Setter
	protected Charset charset = StandardCharsets.UTF_8;

	public CipherEncrypt encrypt() {
		return new CipherEncrypt(manner, symbol, charset);
	}

	public CipherDecrypt decrypt() {
		return new CipherDecrypt(manner, symbol, charset);
	}

	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	public abstract static class AbstractCipherBuilder<B extends AbstractCipherBuilder<B>> {

		/**
		 * 加密方式
		 */
		protected final String manner;

		/**
		 * 加密模式
		 */
		protected String mode;

		/**
		 * 填充模式
		 */
		protected String padding;

		protected String symbol() {
			StringBuilder builder = new StringBuilder(manner);
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

		protected B mode(String mode) {
			this.mode = mode;
			return (B) this;
		}

		public String padding() {
			return padding;
		}

		protected B padding(String padding) {
			this.padding = padding;
			return (B) this;
		}

	}

}
