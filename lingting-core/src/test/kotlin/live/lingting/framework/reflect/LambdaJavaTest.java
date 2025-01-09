package live.lingting.framework.reflect;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024/11/27 22:00
 */
class LambdaJavaTest {

	@Test
	void testJava() {
		JavaLambdaV v = new JavaLambdaV(
			JavaLambdaE::getId,
			JavaLambdaE::getName
		);
		LambdaMeta m0 = LambdaMeta.of(v.getL0());
		LambdaMeta m1 = LambdaMeta.of(v.getL1());
		assertEquals(JavaLambdaE.class, m0.getCls());
		assertEquals(JavaLambdaE.class, m1.getCls());
		assertEquals("id", m0.getField());
		assertEquals("name", m1.getField());
	}

	@Test
	void testKotlin() {
		KotlinLambdaV v = new KotlinLambdaV(
			KotlinLambdaE::getId,
			KotlinLambdaE::getName
		);
		LambdaMeta m0 = LambdaMeta.of(v.getL0());
		LambdaMeta m1 = LambdaMeta.of(v.getL1());
		assertEquals(KotlinLambdaE.class, m0.getCls());
		assertEquals(KotlinLambdaE.class, m1.getCls());
		assertEquals("id", m0.getField());
		assertEquals("name", m1.getField());
	}

	public interface JavaLambdaI<T, R> extends Function<T, R>, Serializable {

	}

	public static class JavaLambdaE {
		String id = "";
		String name = "";

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	public static class JavaLambdaV {
		final JavaLambdaI<JavaLambdaE, ?> l0;
		final JavaLambdaI<JavaLambdaE, ?> l1;

		public JavaLambdaV(JavaLambdaI<JavaLambdaE, ?> l0, JavaLambdaI<JavaLambdaE, ?> l1) {
			this.l0 = l0;
			this.l1 = l1;
		}

		public JavaLambdaI<JavaLambdaE, ?> getL0() {
			return l0;
		}

		public JavaLambdaI<JavaLambdaE, ?> getL1() {
			return l1;
		}
	}

}
