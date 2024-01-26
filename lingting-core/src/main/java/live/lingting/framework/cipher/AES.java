package live.lingting.framework.cipher;

import lombok.Getter;

/**
 * @author lingting 2023-10-13 17:02
 */
public class AES extends AbstractCipher {

	protected AES(String manner, String symbol) {
		super(manner, symbol);
	}

	public static Builder builder() {
		return new Builder();
	}

	@Getter
	public static class Builder extends AbstractCipher.AbstractCipherBuilder<Builder> {

		private Builder() {
			super("AES");
		}

		public AES build() {
			return new AES(manner, symbol());
		}

		public Builder ecb() {
			return mode("ECB");
		}

		public Builder cbc() {
			return mode("CBC");
		}

		public Builder ctr() {
			return mode("CTR");
		}

		public Builder ofb() {
			return mode("OFB");
		}

		public Builder cfb() {
			return mode("CFB");
		}

		public Builder pkcs5() {
			return padding("PKCS5Padding");
		}

		public Builder pkcs7() {
			return padding("PKCS5Padding");
		}

		public Builder iso10126() {
			return padding("ISO10126Padding");
		}

		public Builder no() {
			return padding("NoPadding");
		}

	}

}
