package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2023-12-22 14:07
 */
class ByteUtilsTest {

	final byte byteR = '\r';

	final byte byteN = '\n';

	final byte byteT = '\t';

	@Test
	void isEndLine() {
		assertTrue(ByteUtils.isEndLine(byteR, byteN));
		assertTrue(ByteUtils.isEndLine(byteN));
		assertFalse(ByteUtils.isEndLine(byteN, byteT));
		assertFalse(ByteUtils.isEndLine(byteT));
	}

	@Test
	void isLine() {
		assertTrue(ByteUtils.isLine(Arrays.asList(byteR, byteN)));
		assertTrue(ByteUtils.isLine(Collections.singletonList(byteN)));
		assertTrue(ByteUtils.isLine(Arrays.asList(byteT, byteN)));
		assertFalse(ByteUtils.isLine(Collections.singletonList(byteT)));
	}

	@Test
	void trimEndLine() {
		assertEquals(0, ByteUtils.trimEndLine(Arrays.asList(byteR, byteN)).length);
		assertEquals(0, ByteUtils.trimEndLine(Collections.singletonList(byteN)).length);
		assertEquals(1, ByteUtils.trimEndLine(Arrays.asList(byteT, byteN)).length);
	}

}
