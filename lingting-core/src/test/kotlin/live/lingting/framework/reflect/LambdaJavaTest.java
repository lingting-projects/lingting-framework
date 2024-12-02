package live.lingting.framework.reflect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024/11/27 22:00
 */
class LambdaJavaTest {

	@Test
	void test() {
		LambdaV v = new LambdaV(
			LambdaE::getId,
			LambdaE::getName
		);
		LambdaMeta m0 = LambdaMeta.of(v.getL0());
		LambdaMeta m1 = LambdaMeta.of(v.getL1());
		assertEquals(LambdaE.class, m0.getCls());
		assertEquals(LambdaE.class, m1.getCls());
		assertEquals("id", m0.getField());
		assertEquals("name", m1.getField());
	}
}
