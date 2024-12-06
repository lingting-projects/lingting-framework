package live.lingting.framework.util

import live.lingting.framework.util.AnnotationUtils.findAnnotation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-02-05 14:51
 */
internal class AnnotationUtilsTest {
    @Test
    fun test() {
        assertEquals(A1::class.java, findAnnotation(A3::class.java, A1::class.java)!!.annotationClass.java)
        assertNull(findAnnotation(A3::class.java, A2::class.java))
        assertNull(findAnnotation(P1::class.java, A2::class.java))
        assertEquals(A2::class.java, findAnnotation(I2::class.java, A2::class.java)!!.annotationClass.java)
        assertEquals(A1::class.java, findAnnotation(E1::class.java, A1::class.java)!!.annotationClass.java)
        assertEquals(A2::class.java, findAnnotation(E2::class.java, A2::class.java)!!.annotationClass.java)
        assertEquals(A3::class.java, findAnnotation(E3::class.java, A3::class.java)!!.annotationClass.java)
        assertEquals(A1::class.java, findAnnotation(E4::class.java, A1::class.java)!!.annotationClass.java)
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
