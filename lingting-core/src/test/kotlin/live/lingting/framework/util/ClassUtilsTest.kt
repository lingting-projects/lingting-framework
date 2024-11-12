package live.lingting.framework.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-11 11:18
 */
internal class ClassUtilsTest {
    @Test
    fun test() {
        val className = "live.lingting.framework.util.ClassUtilsTest"
        assertTrue(isPresent(className))
        assertTrue(isPresent(className, ClassLoader.getSystemClassLoader(), ClassUtils::class.java.classLoader))
        assertTrue(isPresent(className, ClassUtilsTest::class.java.classLoader))
        assertFalse(isPresent("live.lingting.framework.mybatis.typehandler.EnumTypeHandler"))
        Assertions.assertEquals(2, ClassUtils.CACHE_CLASS_PRESENT.size)
        val map: Map<ClassLoader, Boolean> = ClassUtils.CACHE_CLASS_PRESENT[className]!!
        val loaders: MutableSet<ClassLoader> = HashSet(3)
        loaders.add(ClassLoader.getSystemClassLoader())
        loaders.add(ClassUtils::class.java.classLoader)
        loaders.add(ClassUtilsTest::class.java.classLoader)
        Assertions.assertEquals(loaders, map.keys)
        Assertions.assertThrows(IllegalArgumentException::class.java) { ClassUtils.isPresent(className, null, null) }
        val scan: Set<Class<Any>> = Assertions.assertDoesNotThrow { ClassUtils.scan("live.lingting.framework") }
        Assertions.assertFalse(scan.isEmpty())
    }
}
