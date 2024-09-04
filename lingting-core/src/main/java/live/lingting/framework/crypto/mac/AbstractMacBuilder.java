package live.lingting.framework.crypto.mac;

import live.lingting.framework.crypto.AbstractCryptoBuilder;

/**
 * @author lingting 2024-09-04 11:31
 */
public class AbstractMacBuilder<B extends AbstractMacBuilder<B>> extends AbstractCryptoBuilder<B, Mac> {

	@Override
	protected Mac doBuild() {
		return new Mac(algorithm, charset, secret, iv);
	}

}
