package live.lingting.framework.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-12-22 14:07
 */
internal class ByteUtilsTest {
    val byteR: Byte = '\r'.code.toByte()

    val byteN: Byte = '\n'.code.toByte()

    val byteT: Byte = '\t'.code.toByte()

    @Test
    fun isEndLine() {
        assertTrue(ByteUtils.isEndLine(byteR, byteN))
        assertTrue(ByteUtils.isEndLine(byteN))
        assertFalse(ByteUtils.isEndLine(byteN, byteT))
        assertFalse(ByteUtils.isEndLine(byteT))
    }

    @Test
    fun isLine() {
        assertTrue(ByteUtils.isLine(listOf(byteR, byteN)))
        assertTrue(ByteUtils.isLine(listOf(byteN)))
        assertTrue(ByteUtils.isLine(listOf(byteT, byteN)))
        assertFalse(ByteUtils.isLine(listOf(byteT)))
    }

    @Test
    fun trimEndLine() {
        assertEquals(0, ByteUtils.trimEndLine(listOf(byteR, byteN)).size)
        assertEquals(0, ByteUtils.trimEndLine(listOf(byteN)).size)
        assertEquals(1, ByteUtils.trimEndLine(listOf(byteT, byteN)).size)
    }
}
