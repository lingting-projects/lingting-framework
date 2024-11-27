package live.lingting.framework.reflect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024/11/27 22:00
 */
public class LambdaJavaTest {

	@Test
	void test() {
		var v = new LambdaV(
			LambdaE::getId,
			LambdaE::getName
		);
		var m0 = LambdaMeta.of(v.getL0());
		var m1 = LambdaMeta.of(v.getL1());
		assertEquals(m0.getCls(), LambdaE.class);
		assertEquals(m1.getCls(), LambdaE.class);
		assertEquals("id", m0.getField());
		assertEquals("name", m1.getField());
	}
}
