package live.lingting.framework.util;

import live.lingting.framework.util.ResourceUtils.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-12 10:54
 */
class ResourceUtilsTest {

	@Test
	@SneakyThrows
	void scan() {
		Collection<Resource> s1 = ResourceUtils.scan(".", r -> !r.isDirectory() && r.getName().startsWith("s"));
		assertEquals(3, s1.size());
		s1.forEach(r -> assertTrue(r.getName().startsWith("s")));

		Collection<Resource> s2 = ResourceUtils.scan(".", r -> !r.isDirectory() && r.getName().endsWith(".sh"));
		assertEquals(2, s2.size());
		s2.forEach(r -> assertTrue(r.getName().endsWith(".sh")));
		for (Resource r : s2) {
			try (InputStream stream = assertDoesNotThrow(r::stream)) {
				String content = assertDoesNotThrow(() -> StreamUtils.toString(stream));
				String trim = content.trim();
				assertEquals(r.getName(), trim);
			}
		}

		Collection<Resource> s3 = ResourceUtils.scan(".", Resource::isDirectory);
		for (Resource r : s3) {
			if (r.isFile()) {
				File file = r.file();
				assertTrue(file.isDirectory());
				assertTrue(file.exists());
			}
		}

		Resource s4 = ResourceUtils.get("scripts/ss1.sh");
		assertNotNull(s4);
		assertEquals("ss1.sh", StreamUtils.toString(s4.stream()).trim());
		Resource s5 = ResourceUtils.get("scripts/ss9.sh");
		assertNull(s5);
	}

}
