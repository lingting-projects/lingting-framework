package live.lingting.framework.util

import live.lingting.framework.data.DataSize
import live.lingting.framework.util.StringUtils.hex
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * @author lingting 2023-10-08 14:41
 */
object DigestUtils {

    @JvmStatic
    fun md5(input: String): ByteArray {
        return md5(input.toByteArray(StandardCharsets.UTF_8))
    }

    @JvmStatic
    fun md5(input: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("MD5")
        return digest.digest(input)
    }

    @JvmStatic
    @JvmOverloads
    fun md5(input: InputStream, size: DataSize = StreamUtils.readSize): ByteArray {
        val digest = MessageDigest.getInstance("MD5")
        StreamUtils.read(input, size) { buffer, len -> digest.update(buffer, 0, len) }
        return digest.digest()
    }

    @JvmStatic
    fun md5Hex(input: String): String {
        return md5Hex(input.toByteArray(StandardCharsets.UTF_8))
    }

    @JvmStatic
    fun md5Hex(input: ByteArray): String {
        val bytes = md5(input)
        return bytes.hex()
    }

    @JvmStatic
    @JvmOverloads
    fun md5Hex(input: InputStream, size: DataSize = StreamUtils.readSize): String {
        val bytes = md5(input, size)
        return bytes.hex()
    }

    @JvmStatic
    fun sha1(input: String): ByteArray {
        return sha1(input.toByteArray(StandardCharsets.UTF_8))
    }

    @JvmStatic
    fun sha1(input: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-1")
        return digest.digest(input)
    }

    @JvmStatic
    @JvmOverloads
    fun sha1(input: InputStream, size: DataSize = StreamUtils.readSize): ByteArray {
        val digest = MessageDigest.getInstance("SHA-1")
        StreamUtils.read(input, size) { buffer, len -> digest.update(buffer, 0, len) }
        return digest.digest()
    }

    @JvmStatic
    fun sha1Hex(input: String): String {
        return sha1Hex(input.toByteArray(StandardCharsets.UTF_8))
    }

    @JvmStatic
    fun sha1Hex(input: ByteArray): String {
        val bytes = sha1(input)
        return bytes.hex()
    }

    @JvmStatic
    @JvmOverloads
    fun sha1Hex(input: InputStream, size: DataSize = StreamUtils.readSize): String {
        val bytes = sha1(input, size)
        return bytes.hex()
    }

    @JvmStatic
    fun sha256(input: String): ByteArray {
        return sha256(input.toByteArray(StandardCharsets.UTF_8))
    }

    @JvmStatic
    fun sha256(input: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input)
    }

    @JvmStatic
    @JvmOverloads
    fun sha256(input: InputStream, size: DataSize = StreamUtils.readSize): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        StreamUtils.read(input, size) { buffer, len -> digest.update(buffer, 0, len) }
        return digest.digest()
    }

    @JvmStatic
    fun sha256Hex(input: String): String {
        return sha256Hex(input.toByteArray(StandardCharsets.UTF_8))
    }

    @JvmStatic
    fun sha256Hex(input: ByteArray): String {
        val bytes = sha256(input)
        return bytes.hex()
    }

    @JvmStatic
    @JvmOverloads
    fun sha256Hex(input: InputStream, size: DataSize = StreamUtils.readSize): String {
        val bytes = sha256(input, size)
        return bytes.hex()
    }
}

