package live.lingting.framework.crypto

import live.lingting.framework.util.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author lingting 2024-09-04 13:47
 */
@Suppress("Unchecked", "kotlin:S6530", "UNCHECKED_CAST")
abstract class AbstractCryptoBuilder<B : AbstractCryptoBuilder<B, R>, R> {
    /**
     * 加密方式
     */
    protected var algorithm: String? = null

    protected var charset: Charset = StandardCharsets.UTF_8

    protected var secret: SecretKeySpec? = null

    protected var iv: IvParameterSpec? = null

    open fun algorithm(algorithm: String): B {
        this.algorithm = algorithm
        return this as B
    }

    fun charset(charset: Charset): B {
        this.charset = charset
        return this as B
    }

    fun secret(secret: SecretKeySpec): B {
        this.secret = secret
        return this as B
    }

    fun secret(secret: ByteArray): B {
        this.secret = SecretKeySpec(secret, algorithm)
        return this as B
    }

    fun secret(secret: String): B {
        val bytes: ByteArray = secret.toByteArray(charset)
        return secret(bytes)
    }

    fun iv(iv: IvParameterSpec?): B {
        this.iv = iv
        return this as B
    }

    fun iv(iv: ByteArray): B {
        this.iv = IvParameterSpec(iv)
        return this as B
    }

    fun iv(iv: String): B {
        val bytes: ByteArray = iv.toByteArray(charset)
        return iv(bytes)
    }

    fun build(): R {
        require(StringUtils.hasText(algorithm)) { "algorithm is required" }
        requireNotNull(secret) { "secret is required" }

        return doBuild()
    }

    protected abstract fun doBuild(): R
}
