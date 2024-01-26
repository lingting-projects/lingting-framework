package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-05 14:42
 */
class FileUtilsTest {

	static final String PATH = "C:\\code\\FileUtils.java";
	static final String URL = "file:///code/FileUtils.java";
	static final String FILENAME = "FileUtils.java";
	static final String EXT = "java";

	@Test
	void getFilename() {
		assertEquals(FILENAME, FileUtils.getFilename(PATH, "\\\\"));
		assertEquals(FILENAME, FileUtils.getFilename(URL, "/"));
		assertEquals(FILENAME, FileUtils.getFilenameByUrl(URL));
	}

	@Test
	void getFileExt() {
		assertEquals(EXT, FileUtils.getFileExt(FileUtils.getFilename(PATH, "\\\\")));
		assertEquals(EXT, FileUtils.getFileExt(FileUtils.getFilename(URL, "/")));
	}

}
