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
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		ClassLoader cuClassLoader = ClassUtils.class.getClassLoader();
		ClassLoader tzClassLoader = ClassUtilsTest.class.getClassLoader();
		Set<ClassLoader> loaders = new HashSet<>(3);
		loaders.add(systemClassLoader);
		loaders.add(cuClassLoader);
		loaders.add(tzClassLoader);

		assertTrue(isPresent(className));
		assertTrue(isPresent(className, systemClassLoader, cuClassLoader));
		assertTrue(isPresent(className, tzClassLoader));
		assertFalse(isPresent("live.lingting.framework.mybatis.typehandler.EnumTypeHandler"));
		Map<ClassLoader, Boolean> map = ClassUtils.CACHE_CLASS_PRESENT.get(className);
		assertEquals(loaders, map.keySet());
		assertThrows(IllegalArgumentException.class, () -> ClassUtils.isPresent(className, null, null));
		Set<Class<Object>> scan = assertDoesNotThrow(() -> ClassUtils.scan("live.lingting.framework"));
		assertFalse(scan.isEmpty());
	}

}
