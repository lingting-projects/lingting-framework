package live.lingting.framework.util

import live.lingting.framework.util.ClassUtils.exists
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

/**
 * @author lingting 2024-09-11 11:18
 */
class ClassUtilsTest {
    @Test
    fun test() {
        val classes = ClassUtils.scan<Any>("")
        assertFalse(classes.isEmpty())

        val classLoader = ClassUtils::class.java.classLoader
        val testClassLoader = ClassUtilsTest::class.java.classLoader
        val systemClassLoader = ClassLoader.getSystemClassLoader()

        val className = "live.lingting.framework.util.ClassUtilsTest"
        val className2 = "live.lingting.framework.mybatis.typehandler.EnumTypeHandler"
        assertTrue(exists(className))
        assertTrue(exists(className, systemClassLoader, classLoader))
        assertTrue(exists(className, testClassLoader))
        assertFalse(exists(className2))
        val loaders: MutableSet<ClassLoader> = HashSet(3)
        loaders.add(systemClassLoader)
        loaders.add(ClassUtils::class.java.classLoader)
        loaders.add(ClassUtilsTest::class.java.classLoader)

        val scan: Set<Class<Any>> = assertDoesNotThrow(ThrowingSupplier { ClassUtils.scan("live.lingting.framework") })
        assertFalse(scan.isEmpty())
        val typesN = ClassUtils.typeArguments(N::class)
        assertTrue(typesN.isEmpty())
        val typesS1 = ClassUtils.typeArguments(S1::class)
        assertEquals(1, typesS1.size)
        assertEquals(N::class.java, typesS1[0])
        val typesI1 = ClassUtils.typeArguments(I1::class)
        assertEquals(1, typesI1.size)
        assertEquals(N::class.java, typesI1[0])
        val typesSI = ClassUtils.typeArguments(SI::class)
        assertEquals(2, typesSI.size)
        assertEquals(S1::class.java, typesSI[0])
        assertEquals(I1::class.java, typesSI[1])
    }
}

class N
open class S<E>
class S1 : S<N>()
interface I<E>
class I1 : I<N>
class SI : S<S1>(), I<I1>
