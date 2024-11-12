package live.lingting.framework.crypto

import live.lingting.framework.api.R
import java.nio.charset.Charset
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author lingting 2024-09-04 13:59
 */
abstract class AbstractCrypt<R : AbstractCrypt<R>>(
    protected val algorithm: String,
    protected val charset: Charset,
    protected val secret: SecretKeySpec,
    protected val iv: IvParameterSpec?,
) {
    fun useSecret(secret: SecretKeySpec): R {
        return instance(algorithm, charset, secret, iv)
    }

    fun useSecret(secret: ByteArray): R {
        val spec = SecretKeySpec(secret, algorithm)
        return useSecret(spec)
    }

    fun useSecret(secret: String): R {
        val bytes: ByteArray = secret.toByteArray(charset)
        return useSecret(bytes)
    }

    fun useIv(iv: IvParameterSpec?): R {
        return instance(algorithm, charset, secret, iv)
    }

    fun useIv(iv: ByteArray): R {
        val spec = IvParameterSpec(iv)
        return useIv(spec)
    }

    fun useIv(iv: String): R {
        val bytes: ByteArray = iv.toByteArray(charset)
        return useIv(bytes)
    }

    protected abstract fun instance(algorithm: String, charset: Charset, secret: SecretKeySpec, iv: IvParameterSpec?): R
}
