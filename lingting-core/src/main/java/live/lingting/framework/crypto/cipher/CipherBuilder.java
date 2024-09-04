package live.lingting.framework.crypto.cipher;

/**
 * @author lingting 2024-09-04 11:26
 */
public class CipherBuilder extends AbstractCipherBuilder<CipherBuilder> {

	public abstract static class SpecificCipherBuilder<B extends SpecificCipherBuilder<B>>
			extends AbstractCipherBuilder<B> {

		protected SpecificCipherBuilder(String algorithm) {
			this.algorithm = algorithm;
		}

		@Override
		public B algorithm(String algorithm) {
			return (B) this;
		}

	}

	public static class AES extends SpecificCipherBuilder<AES> {

		public AES() {
			super("AES");
		}

		public CipherBuilder.AES ecb() {
			return mode("ECB");
		}

		public CipherBuilder.AES cbc() {
			return mode("CBC");
		}

		public CipherBuilder.AES ctr() {
			return mode("CTR");
		}

		public CipherBuilder.AES ofb() {
			return mode("OFB");
		}

		public CipherBuilder.AES cfb() {
			return mode("CFB");
		}

		public CipherBuilder.AES pkcs5() {
			return padding("PKCS5Padding");
		}

		public CipherBuilder.AES pkcs7() {
			return padding("PKCS5Padding");
		}

		public CipherBuilder.AES iso10126() {
			return padding("ISO10126Padding");
		}

		public CipherBuilder.AES no() {
			return padding("NoPadding");
		}

	}

}
