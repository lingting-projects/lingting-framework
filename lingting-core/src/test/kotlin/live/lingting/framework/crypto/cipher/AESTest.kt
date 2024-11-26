package live.lingting.framework.crypto.cipher

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-04 11:36
 */
internal class AESTest {
    @Test
    fun defaultTest() {
        val secret = "6A921171B0A28CC2"
        val plaintext = "a123456"
        val ciphertext = "dIW9PQf3/GFbuhhtw252yw=="

        val aes1 = Cipher.aesBuilder().secret(secret).build()
        val e1 = aes1.encryptBase64(plaintext)
        assertEquals(ciphertext, e1)

        val d1 = aes1.decryptBase64(ciphertext)
        assertEquals(plaintext, d1)

        // java 中 默认Cipher 使用 ECB + pkcs7
        val aes2 = Cipher.aesBuilder().secret(secret).ecb().pkcs7().build()
        val e21 = aes2.encryptBase64(plaintext)
        assertEquals(ciphertext, e21)

        val d21 = aes2.decryptBase64(ciphertext)
        assertEquals(plaintext, d21)

        // pkcs7 和 pkcs5 等效
        val aes3 = Cipher.aesBuilder().secret(secret).ecb().pkcs7().build()
        val e31 = aes3.encryptBase64(plaintext)
        assertEquals(ciphertext, e31)

        val d31 = aes3.decryptBase64(ciphertext)
        assertEquals(plaintext, d31)
    }

    @Test
    fun ecb() {
        val secret = "6A921171B0A28CC2"
        val plaintext = "a123456"

        val ciphertextPkcs5 = "dIW9PQf3/GFbuhhtw252yw=="
        var aes = Cipher.aesBuilder().ecb().pkcs5().secret(secret).build()
        assertEquals(ciphertextPkcs5, aes.encryptBase64(plaintext))
        assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs5))

        val ciphertextPkcs7 = "dIW9PQf3/GFbuhhtw252yw=="
        aes = Cipher.aesBuilder().ecb().pkcs7().secret(secret).build()
        assertEquals(ciphertextPkcs7, aes.encryptBase64(plaintext))
        assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs7))

        val ciphertextIso10126 = "nKnxu8X+MTPePKWnOffLBQ=="
        aes = Cipher.aesBuilder().ecb().iso10126().secret(secret).build()
        // 填入的是随机字节, 所有每次加密结果都不一样, 只需要校验解密
        assertEquals(plaintext, aes.decryptBase64(ciphertextIso10126))

        // 无填充要求明文长度是16字节的倍数
        val plaintextNo = "1234567890123456"
        val ciphertextNo = "Vk90UnG6Meq3uxQJaWl7EQ=="
        aes = Cipher.aesBuilder().ecb().no().secret(secret).build()
        assertEquals(ciphertextNo, aes.encryptBase64(plaintextNo))
        assertEquals(plaintextNo, aes.decryptBase64(ciphertextNo))
    }

    @Test
    fun cbc() {
        val iv = "9A221171B0A18CC2"
        val secret = "6A921171B0A28CC2"
        val plaintext = "a123456"

        val ciphertextPkcs5 = "vlqZ2ozl5muE1XOj3Srh1g=="
        var aes = Cipher.aesBuilder().cbc().pkcs5().secret(secret).iv(iv).build()
        assertEquals(ciphertextPkcs5, aes.encryptBase64(plaintext))
        assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs5))

        val ciphertextPkcs7 = "vlqZ2ozl5muE1XOj3Srh1g=="
        aes = Cipher.aesBuilder().cbc().pkcs7().secret(secret).iv(iv).build()
        assertEquals(ciphertextPkcs7, aes.encryptBase64(plaintext))
        assertEquals(plaintext, aes.decryptBase64(ciphertextPkcs7))

        val ciphertextIso10126 = "0jdg0wcVcdAzAqewb5LENA=="
        aes = Cipher.aesBuilder().cbc().iso10126().secret(secret).iv(iv).build()
        assertEquals(plaintext, aes.decryptBase64(ciphertextIso10126))

        // 无填充要求明文长度是16字节的倍数
        val plaintextNo = "1234567890123456"
        val ciphertextNo = "eoTQrLWSu1cYUro/HaHzIw=="
        aes = Cipher.aesBuilder().cbc().no().secret(secret).iv(iv).build()
        assertEquals(ciphertextNo, aes.encryptBase64(plaintextNo))
        assertEquals(plaintextNo, aes.decryptBase64(ciphertextNo))
    }
}
