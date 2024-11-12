package live.lingting.framework.crypto.mac

import live.lingting.framework.crypto.AbstractCryptoBuilder

/**
 * @author lingting 2024-09-04 11:31
 */
open class AbstractMacBuilder<B : AbstractMacBuilder<B>> : AbstractCryptoBuilder<B, Mac>() {
    override fun doBuild(): Mac {
        return Mac(algorithm, charset, secret, iv)
    }
}
