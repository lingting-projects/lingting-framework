package live.lingting.framework.util

import org.junit.jupiter.api.Test


/**
 * @author lingting 2023-12-22 14:07
 */
internal class ByteUtilsTest {
    val byteR: Byte = '\r'.code.toByte()

    val byteN: Byte = '\n'.code.toByte()

    val byteT: Byte = '\t'.code.toByte()

    @get:Test
    val isEndLine: Unit
        get() {
            assertTrue(ByteUtils.isEndLine(byteR, byteN))
            assertTrue(ByteUtils.isEndLine(byteN))
            assertFalse(ByteUtils.isEndLine(byteN, byteT))
            assertFalse(ByteUtils.isEndLine(byteT))
        }

    @get:Test
    val isLine: Unit
        get() {
            assertTrue(ByteUtils.isLine(Arrays.asList<T>(byteR, byteN)))
            assertTrue(ByteUtils.isLine(listOf<T>(byteN)))
            assertTrue(ByteUtils.isLine(Arrays.asList<T>(byteT, byteN)))
            assertFalse(ByteUtils.isLine(listOf<T>(byteT)))
        }

    @Test
    fun trimEndLine() {
        assertEquals(0, ByteUtils.trimEndLine(Arrays.asList<T>(byteR, byteN)).length)
        assertEquals(0, ByteUtils.trimEndLine(listOf<T>(byteN)).length)
        assertEquals(1, ByteUtils.trimEndLine(Arrays.asList<T>(byteT, byteN)).length)
    }
}
