package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static live.lingting.framework.util.ClassUtils.isPresent;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-11 11:18
 */
class ClassUtilsTest {

	@Test
	void test() {
		String className = "live.lingting.framework.util.ClassUtilsTest";
		assertTrue(isPresent(className));
		assertTrue(isPresent(className, ClassLoader.getSystemClassLoader(), ClassUtils.class.getClassLoader()));
		assertTrue(isPresent(className, ClassUtilsTest.class.getClassLoader()));
		assertFalse(isPresent("live.lingting.framework.mybatis.typehandler.EnumTypeHandler"));
		assertEquals(2, ClassUtils.CACHE_CLASS_PRESENT.size());
		Map<ClassLoader, Boolean> map = ClassUtils.CACHE_CLASS_PRESENT.get(className);
		Set<ClassLoader> loaders = new HashSet<>(3);
		loaders.add(ClassLoader.getSystemClassLoader());
		loaders.add(ClassUtils.class.getClassLoader());
		loaders.add(ClassUtilsTest.class.getClassLoader());
		assertEquals(loaders, map.keySet());
		assertThrows(IllegalArgumentException.class, () -> ClassUtils.isPresent(className, null, null));
		Set<Class<Object>> scan = assertDoesNotThrow(() -> ClassUtils.scan("live.lingting.framework"));
		assertFalse(scan.isEmpty());
	}

}
