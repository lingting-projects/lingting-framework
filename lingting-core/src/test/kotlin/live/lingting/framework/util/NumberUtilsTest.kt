package live.lingting.framework.util

import java.math.BigDecimal
import java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-11-24 13:34
 */
internal class NumberUtilsTest {
    @Test
    fun isInteger() {
        assertTrue(NumberUtils.isInteger(2))
        assertTrue(NumberUtils.isInteger(BigDecimal("2.0000")))
        assertFalse(NumberUtils.isInteger(BigDecimal("2.0010")))
        assertFalse(NumberUtils.isInteger(2.7))
    }

    @Test
    fun isBig() {
        assertTrue(NumberUtils.isBig(BigDecimal("2.0000")))
        assertFalse(NumberUtils.isBig(2.7))
    }

    @Test
    fun isPower2() {
        assertTrue(NumberUtils.isPower2(2))
        assertTrue(NumberUtils.isPower2(BigDecimal("2.0000")))
        assertFalse(NumberUtils.isPower2(BigDecimal("2.0010")))
        assertFalse(NumberUtils.isPower2(2.7))
    }

    @Test
    fun isEven() {
        assertTrue(NumberUtils.isEven(2))
        assertTrue(NumberUtils.isEven(BigDecimal("2.0000")))
        assertFalse(NumberUtils.isEven(BigDecimal("2.0010")))
        assertFalse(NumberUtils.isEven(2.7))
    }

    @Test
    fun bitLength() {
        assertEquals(2, NumberUtils.bitLength(2))
        assertEquals(2, NumberUtils.bitLength(3))
        assertEquals(7, NumberUtils.bitLength(BigDecimal("80.0000")))
        assertEquals(7, NumberUtils.bitLength(BigDecimal("80.00300")))
    }

    @Test
    fun nextPower2() {
        assertEquals(NumberUtils.INTEGER_TWO, NumberUtils.nextPower2(2))
        assertEquals(BigInteger("4"), NumberUtils.nextPower2(3))
        assertEquals(BigInteger("64"), NumberUtils.nextPower2(56))
    }
}
