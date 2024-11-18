package live.lingting.framework.crypto.mac

import java.nio.charset.Charset
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import live.lingting.framework.crypto.AbstractCrypt
import live.lingting.framework.crypto.mac.MacBuilder.Hmac
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024-09-04 11:52
 */
class Mac(algorithm: String, charset: Charset, secret: SecretKeySpec, iv: IvParameterSpec?) : AbstractCrypt<Mac>(algorithm, charset, secret, iv) {
    override fun instance(algorithm: String, charset: Charset, secret: SecretKeySpec, iv: IvParameterSpec?): Mac {
        return Mac(algorithm, charset, secret, iv)
    }

    // region builder
    fun toBuilder(): MacBuilder {
        return MacBuilder().algorithm(algorithm).charset(charset).secret(secret).iv(iv)
    }

    // endregion

    fun instance(): javax.crypto.Mac {
        return javax.crypto.Mac.getInstance(algorithm)
    }


    fun mac(): javax.crypto.Mac {
        val mac = instance()
        if (iv == null) {
            mac.init(secret)
        } else {
            mac.init(secret, iv)
        }
        return mac
    }


    fun calculate(bytes: ByteArray): ByteArray {
        val mac = mac()
        return mac.doFinal(bytes)
    }


    fun calculate(source: String): ByteArray {
        val bytes: ByteArray = source.toByteArray(charset)
        return calculate(bytes)
    }


    fun calculateString(bytes: ByteArray): String {
        val calculate = calculate(bytes)
        return String(calculate, charset)
    }


    fun calculateString(source: String): String {
        val bytes: ByteArray = source.toByteArray(charset)
        return calculateString(bytes)
    }


    fun calculateBase64(bytes: ByteArray): String {
        val calculate = calculate(bytes)
        return StringUtils.base64(calculate)
    }


    fun calculateBase64(source: String): String {
        val bytes: ByteArray = source.toByteArray(charset)
        return calculateBase64(bytes)
    }


    fun calculateHex(bytes: ByteArray): String {
        val calculate = calculate(bytes)
        return StringUtils.hex(calculate)
    }


    fun calculateHex(source: String): String {
        val bytes: ByteArray = source.toByteArray(charset)
        return calculateHex(bytes)
    }

    companion object {
        @JvmStatic
        fun builder(): MacBuilder {
            return MacBuilder()
        }


        @JvmStatic
        fun hmacBuilder(): Hmac {
            return Hmac()
        }
    }
}
