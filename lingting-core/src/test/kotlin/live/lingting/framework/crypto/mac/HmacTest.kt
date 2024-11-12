package live.lingting.framework.crypto.mac

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


/**
 * @author lingting 2024-09-04 14:06
 */
internal class HmacTest {
    var source: String = "hello"

    var secret: String = "secret"


    @Test
    fun sha1() {
        val mac = Mac.hmacBuilder().sha1().secret(secret).build()
        Assertions.assertEquals("URIFXAX5RPhXVe/FzYlw4ZTp9Fs=", mac.calculateBase64(source))
        Assertions.assertEquals("5112055c05f944f85755efc5cd8970e194e9f45b", mac.calculateHex(source))
    }


    @Test
    fun sha256() {
        val mac = Mac.hmacBuilder().sha256().secret(secret).build()
        Assertions.assertEquals("iKqz7ejTrflNJquQ07r9SiCDBww7zOnAFO4EpEOEfAs=", mac.calculateBase64(source))
        Assertions.assertEquals("88aab3ede8d3adf94d26ab90d3bafd4a2083070c3bcce9c014ee04a443847c0b", mac.calculateHex(source))
    }
}
