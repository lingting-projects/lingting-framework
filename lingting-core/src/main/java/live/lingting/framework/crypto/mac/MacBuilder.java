package live.lingting.framework.crypto.mac;

/**
 * @author lingting 2024-09-04 13:42
 */
public class MacBuilder extends AbstractMacBuilder<MacBuilder> {

	public static class Hmac extends AbstractMacBuilder<Hmac> {

		public Hmac sha256() {
			return algorithm("HmacSHA256");
		}

		public Hmac sha1() {
			return algorithm("HmacSHA1");
		}

	}

}
