package live.lingting.framework.crypto.cipher

import live.lingting.framework.crypto.AbstractCrypt
import live.lingting.framework.util.StringUtils
import java.nio.charset.Charset
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author lingting 2024-09-04 10:17
 */
class Cipher(
    algorithm: String,
    /**
     * 加密模式
     */
    val mode: String,
    /**
     * 填充模式
     */
    val padding: String,
    /**
     * 加密具体行为, 如: AES/ECB/NoPadding
     */
    val symbol: String,
    charset: Charset, secret: SecretKeySpec,
    iv: IvParameterSpec?,
) : AbstractCrypt<Cipher>(algorithm, charset, secret, iv) {

    override fun instance(algorithm: String, charset: Charset, secret: SecretKeySpec, iv: IvParameterSpec?): Cipher {
        return Cipher(algorithm, mode, padding, symbol, charset, secret, iv)
    }


    fun instance(): javax.crypto.Cipher {
        return javax.crypto.Cipher.getInstance(symbol)
    }

    /**
     * @param mode [javax.crypto.Cipher.ENCRYPT_MODE]
     */

    fun cipher(mode: Int): javax.crypto.Cipher {
        val cipher = instance()
        if (iv == null) {
            cipher.init(mode, secret)
        } else {
            cipher.init(mode, secret, iv)
        }
        return cipher
    }

    // region builder
    fun toBuilder(): CipherBuilder {
        return builder().algorithm(algorithm).mode(mode).padding(padding).charset(charset).secret(secret).iv(iv)
    }

    // endregion
    // region encrypt

    fun encrypt(bytes: ByteArray): ByteArray {
        val cipher = cipher(javax.crypto.Cipher.ENCRYPT_MODE)
        return cipher.doFinal(bytes)
    }


    fun encrypt(plaintext: String): ByteArray {
        val bytes: ByteArray = plaintext.toByteArray(charset)
        return encrypt(bytes)
    }


    fun encryptString(bytes: ByteArray): String {
        val encrypt = encrypt(bytes)
        return String(encrypt, charset)
    }


    fun encryptBase64(bytes: ByteArray): String {
        val encrypt = encrypt(bytes)
        return StringUtils.base64(encrypt)
    }


    fun encryptBase64(plaintext: String): String {
        val bytes: ByteArray = plaintext.toByteArray(charset)
        return encryptBase64(bytes)
    }


    fun encryptHex(bytes: ByteArray): String {
        val encrypt = encrypt(bytes)
        return StringUtils.hex(encrypt)
    }


    fun encryptHex(plaintext: String): String {
        val bytes: ByteArray = plaintext.toByteArray(charset)
        return encryptHex(bytes)
    }

    // endregion
    // region decrypt

    fun decrypt(bytes: ByteArray): ByteArray {
        val cipher = cipher(javax.crypto.Cipher.DECRYPT_MODE)
        return cipher.doFinal(bytes)
    }


    fun decrypt(ciphertext: String): ByteArray {
        val bytes: ByteArray = ciphertext.toByteArray(charset)
        return decrypt(bytes)
    }


    fun decryptString(bytes: ByteArray): String {
        val decrypt = decrypt(bytes)
        return String(decrypt, charset)
    }


    fun decryptBase64(bytes: ByteArray): String {
        return decryptString(bytes)
    }


    fun decryptBase64(ciphertext: String): String {
        val bytes: ByteArray = StringUtils.base64(ciphertext)
        return decryptBase64(bytes)
    }


    fun decryptHex(bytes: ByteArray): String {
        return decryptString(bytes)
    }


    fun decryptHex(ciphertext: String): String {
        val bytes: ByteArray = StringUtils.hex(ciphertext)
        return decryptHex(bytes)
    }
    // endregion

    companion object {
        fun builder(): CipherBuilder {
            return CipherBuilder()
        }


        fun aesBuilder(): CipherBuilder.AES {
            return CipherBuilder.AES()
        }

        fun aes(): Cipher {
            return aesBuilder().build()
        }
    }
}
