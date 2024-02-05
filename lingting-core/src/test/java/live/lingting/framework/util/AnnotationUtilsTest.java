package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

import static live.lingting.framework.util.AnnotationUtils.findAnnotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author lingting 2024-02-05 14:51
 */
class AnnotationUtilsTest {

	@Test
	void test() {
		assertEquals(A1.class, Objects.requireNonNull(findAnnotation(A3.class, A1.class)).annotationType());
		assertNull(findAnnotation(A3.class, A2.class));
		assertNull(findAnnotation(P1.class, A2.class));
		assertEquals(A2.class, Objects.requireNonNull(findAnnotation(I2.class, A2.class)).annotationType());
		assertEquals(A1.class, Objects.requireNonNull(findAnnotation(E1.class, A1.class)).annotationType());
		assertEquals(A2.class, Objects.requireNonNull(findAnnotation(E2.class, A2.class)).annotationType());
		assertEquals(A3.class, Objects.requireNonNull(findAnnotation(E3.class, A3.class)).annotationType());
		assertEquals(A1.class, Objects.requireNonNull(findAnnotation(E4.class, A1.class)).annotationType());
		assertNull(findAnnotation((AnnotatedElement) E4.class, A1.class));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface A1 {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface A2 {

	}

	@A1
	@Retention(RetentionPolicy.RUNTIME)
	@interface A3 {

	}

	static class P1 {

	}

	@A2
	static class P2 {

	}

	@A1
	interface I1 {

	}

	@A2
	interface I2 {

	}

	interface I3 extends I1 {

	}

	static class E1 extends P1 implements I1 {

	}

	static class E2 extends P2 {

	}

	@A3
	static class E3 extends P2 {

	}

	static class E4 extends P1 implements I3 {

	}

}
