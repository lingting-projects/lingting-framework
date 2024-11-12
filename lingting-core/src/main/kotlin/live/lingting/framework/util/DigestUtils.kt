package live.lingting.framework.util

import live.lingting.framework.function.ThrowingBiConsumerE
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * @author lingting 2023-10-08 14:41
 */
class DigestUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {

        fun md5(input: String): ByteArray {
            return md5(input.toByteArray(StandardCharsets.UTF_8))
        }


        fun md5(input: ByteArray?): ByteArray {
            val digest = MessageDigest.getInstance("MD5")
            return digest.digest(input)
        }


        fun md5(input: InputStream, size: Int = StreamUtils.getReadSize()): ByteArray {
            val digest = MessageDigest.getInstance("MD5")
            StreamUtils.read(input, size, ThrowingBiConsumerE<ByteArray?, Int?, IOException?> { buffer: ByteArray?, len: Int? -> digest.update(buffer, 0, len!!) })
            return digest.digest()
        }


        fun md5Hex(input: String): String {
            return md5Hex(input.toByteArray(StandardCharsets.UTF_8))
        }


        fun md5Hex(input: ByteArray?): String {
            val bytes = md5(input)
            return StringUtils.hex(bytes)
        }


        fun md5Hex(input: InputStream, size: Int = StreamUtils.getReadSize()): String {
            val bytes = md5(input, size)
            return StringUtils.hex(bytes)
        }


        fun sha1(input: String): ByteArray {
            return sha1(input.toByteArray(StandardCharsets.UTF_8))
        }


        fun sha1(input: ByteArray?): ByteArray {
            val digest = MessageDigest.getInstance("SHA-1")
            return digest.digest(input)
        }


        fun sha1(input: InputStream, size: Int = StreamUtils.getReadSize()): ByteArray {
            val digest = MessageDigest.getInstance("SHA-1")
            StreamUtils.read(input, size, ThrowingBiConsumerE<ByteArray?, Int?, IOException?> { buffer: ByteArray?, len: Int? -> digest.update(buffer, 0, len!!) })
            return digest.digest()
        }


        fun sha1Hex(input: String): String {
            return sha1Hex(input.toByteArray(StandardCharsets.UTF_8))
        }


        fun sha1Hex(input: ByteArray?): String {
            val bytes = sha1(input)
            return StringUtils.hex(bytes)
        }


        fun sha1Hex(input: InputStream, size: Int = StreamUtils.getReadSize()): String {
            val bytes = sha1(input, size)
            return StringUtils.hex(bytes)
        }


        fun sha256(input: String): ByteArray {
            return sha256(input.toByteArray(StandardCharsets.UTF_8))
        }


        fun sha256(input: ByteArray?): ByteArray {
            val digest = MessageDigest.getInstance("SHA-256")
            return digest.digest(input)
        }


        fun sha256(input: InputStream, size: Int = StreamUtils.getReadSize()): ByteArray {
            val digest = MessageDigest.getInstance("SHA-256")
            StreamUtils.read(input, size, ThrowingBiConsumerE<ByteArray?, Int?, IOException?> { buffer: ByteArray?, len: Int? -> digest.update(buffer, 0, len!!) })
            return digest.digest()
        }


        fun sha256Hex(input: String): String {
            return sha256Hex(input.toByteArray(StandardCharsets.UTF_8))
        }


        fun sha256Hex(input: ByteArray?): String {
            val bytes = sha256(input)
            return StringUtils.hex(bytes)
        }


        fun sha256Hex(input: InputStream, size: Int = StreamUtils.getReadSize()): String {
            val bytes = sha256(input, size)
            return StringUtils.hex(bytes)
        }
    }
}
