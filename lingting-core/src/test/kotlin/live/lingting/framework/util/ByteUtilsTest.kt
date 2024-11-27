package live.lingting.framework.util

import live.lingting.framework.util.ByteUtils.isEndLine
import live.lingting.framework.util.ByteUtils.isLine
import live.lingting.framework.util.ByteUtils.trimEndLine
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
        assertTrue(isEndLine(byteR, byteN))
        assertTrue(isEndLine(byteN))
        assertFalse(isEndLine(byteN, byteT))
        assertFalse(isEndLine(byteT))
    }

    @Test
    fun isLine() {
        assertTrue(isLine(listOf(byteR, byteN)))
        assertTrue(isLine(listOf(byteN)))
        assertTrue(isLine(listOf(byteT, byteN)))
        assertFalse(isLine(listOf(byteT)))
    }

    @Test
    fun trimEndLine() {
        assertEquals(0, trimEndLine(listOf(byteR, byteN)).size)
        assertEquals(0, trimEndLine(listOf(byteN)).size)
        assertEquals(1, trimEndLine(listOf(byteT, byteN)).size)
    }
}
