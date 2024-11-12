package live.lingting.framework.crypto.cipher

import live.lingting.framework.crypto.AbstractCryptoBuilder
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024-09-04 11:31
 */
open class AbstractCipherBuilder<B : AbstractCipherBuilder<B>> : AbstractCryptoBuilder<B, Cipher>() {
    /**
     * 加密模式
     */
    protected var mode: String? = null

    /**
     * 填充模式
     */
    protected var padding: String? = null

    fun symbol(): String {
        val builder = StringBuilder(algorithm)
        if (StringUtils.hasText(mode)) {
            builder.append("/").append(mode)
        }
        if (StringUtils.hasText(padding)) {
            builder.append("/").append(padding)
        }
        return builder.toString()
    }

    fun mode(): String? {
        return mode
    }

    fun padding(): String? {
        return padding
    }

    fun mode(mode: String): B {
        this.mode = mode
        return this as B
    }

    fun padding(padding: String): B {
        this.padding = padding
        return this as B
    }

    override fun doBuild(): Cipher {
        val symbol = symbol()
        return Cipher(algorithm, mode, padding, symbol, charset, secret, iv)
    }
}
