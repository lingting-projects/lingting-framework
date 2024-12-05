package live.lingting.framework.util

import live.lingting.framework.util.ClassUtils.exists
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
        assertNotNull(ClassUtils.CACHE_CLASS_PRESENT[className])
        assertNotNull(ClassUtils.CACHE_CLASS_PRESENT[className2])
        val map: Map<ClassLoader, Boolean> = ClassUtils.CACHE_CLASS_PRESENT[className]!!
        val loaders: MutableSet<ClassLoader> = HashSet(3)
        loaders.add(systemClassLoader)
        loaders.add(ClassUtils::class.java.classLoader)
        loaders.add(ClassUtilsTest::class.java.classLoader)
        assertEquals(loaders, map.keys)
        assertThrows(IllegalArgumentException::class.java) { exists(className, null, null) }

        val scan: Set<Class<Any>> = assertDoesNotThrow(ThrowingSupplier { ClassUtils.scan<Any>("live.lingting.framework") })
        assertFalse(scan.isEmpty())
    }
}
