package live.lingting.framework.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.reflect.AnnotatedElement


/**
 * @author lingting 2024-02-05 14:51
 */
internal class AnnotationUtilsTest {
    @Test
    fun test() {
        assertEquals(A1::class.java, Objects.requireNonNull<T>(findAnnotation(A3::class.java, A1::class.java)).annotationType())
        Assertions.assertNull(findAnnotation(A3::class.java, A2::class.java))
        Assertions.assertNull(findAnnotation(P1::class.java, A2::class.java))
        assertEquals(A2::class.java, Objects.requireNonNull<T>(findAnnotation(I2::class.java, A2::class.java)).annotationType())
        assertEquals(A1::class.java, Objects.requireNonNull<T>(findAnnotation(E1::class.java, A1::class.java)).annotationType())
        assertEquals(A2::class.java, Objects.requireNonNull<T>(findAnnotation(E2::class.java, A2::class.java)).annotationType())
        assertEquals(A3::class.java, Objects.requireNonNull<T>(findAnnotation(E3::class.java, A3::class.java)).annotationType())
        assertEquals(A1::class.java, Objects.requireNonNull<T>(findAnnotation(E4::class.java, A1::class.java)).annotationType())
        Assertions.assertNull(findAnnotation(E4::class.java as AnnotatedElement, A1::class.java))
    }

    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class A1

    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class A2

    @A1
    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class A3

    @A1
    internal interface I1

    @A2
    internal interface I2

    internal interface I3 : I1

    internal open class P1

    @A2
    internal open class P2

    internal class E1 : P1(), I1

    internal class E2 : P2()

    @A3
    internal class E3 : P2()

    internal class E4 : P1(), I3
}
