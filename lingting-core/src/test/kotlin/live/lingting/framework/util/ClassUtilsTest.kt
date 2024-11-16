package live.lingting.framework.util

import live.lingting.framework.util.ClassUtils.isPresent
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

/**
 * @author lingting 2024-09-11 11:18
 */
internal class ClassUtilsTest {
    @Test
    fun test() {
        val classLoader = ClassUtils::class.java.classLoader
        val testClassLoader = ClassUtilsTest::class.java.classLoader
        val systemClassLoader = ClassLoader.getSystemClassLoader()

        val className = "live.lingting.framework.util.ClassUtilsTest"
        val className2 = "live.lingting.framework.mybatis.typehandler.EnumTypeHandler"
        assertTrue(isPresent(className))
        assertTrue(isPresent(className, systemClassLoader, classLoader))
        assertTrue(isPresent(className, testClassLoader))
        assertFalse(isPresent(className2))
        assertNotNull(ClassUtils.CACHE_CLASS_PRESENT[className])
        assertNotNull(ClassUtils.CACHE_CLASS_PRESENT[className2])
        val map: Map<ClassLoader, Boolean> = ClassUtils.CACHE_CLASS_PRESENT[className]!!
        val loaders: MutableSet<ClassLoader> = HashSet(3)
        loaders.add(systemClassLoader)
        loaders.add(ClassUtils::class.java.classLoader)
        loaders.add(ClassUtilsTest::class.java.classLoader)
        assertEquals(loaders, map.keys)
        assertThrows(IllegalArgumentException::class.java) { isPresent(className, null, null) }

        val supplier: ThrowingSupplier<Set<Class<Any>>> = object : ThrowingSupplier<Set<Class<Any>>> {
            override fun get(): Set<Class<Any>> {
                return ClassUtils.scan<Any>("live.lingting.framework")
            }
        }
        val scan: Set<Class<Any>> = assertDoesNotThrow(supplier)
        assertFalse(scan.isEmpty())
    }
}
