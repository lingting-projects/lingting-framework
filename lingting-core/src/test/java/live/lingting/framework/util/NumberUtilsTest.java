package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2023-11-24 13:34
 */
class NumberUtilsTest {

	@Test
	void isInteger() {
		assertTrue(NumberUtils.isInteger(2));
		assertTrue(NumberUtils.isInteger(new BigDecimal("2.0000")));
		assertFalse(NumberUtils.isInteger(new BigDecimal("2.0010")));
		assertFalse(NumberUtils.isInteger(2.7));
	}

	@Test
	void isBig() {
		assertTrue(NumberUtils.isBig(new BigDecimal("2.0000")));
		assertFalse(NumberUtils.isBig(2.7));
	}

	@Test
	void isPower2() {
		assertTrue(NumberUtils.isPower2(2));
		assertTrue(NumberUtils.isPower2(new BigDecimal("2.0000")));
		assertFalse(NumberUtils.isPower2(new BigDecimal("2.0010")));
		assertFalse(NumberUtils.isPower2(2.7));
	}

	@Test
	void isEven() {
		assertTrue(NumberUtils.isEven(2));
		assertTrue(NumberUtils.isEven(new BigDecimal("2.0000")));
		assertFalse(NumberUtils.isEven(new BigDecimal("2.0010")));
		assertFalse(NumberUtils.isEven(2.7));
	}

	@Test
	void bitLength() {
		assertEquals(2, NumberUtils.bitLength(2));
		assertEquals(2, NumberUtils.bitLength(3));
		assertEquals(7, NumberUtils.bitLength(new BigDecimal("80.0000")));
		assertEquals(7, NumberUtils.bitLength(new BigDecimal("80.00300")));
	}

	@Test
	void nextPower2() {
		assertEquals(NumberUtils.INTEGER_TWO, NumberUtils.nextPower2(2));
		assertEquals(new BigInteger("4"), NumberUtils.nextPower2(3));
		assertEquals(new BigInteger("64"), NumberUtils.nextPower2(56));
	}

}
