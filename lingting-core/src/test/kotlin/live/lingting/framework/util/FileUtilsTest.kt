package live.lingting.framework.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-05 14:42
 */
internal class FileUtilsTest {
    @Test
    fun filename() {
        assertEquals(FILENAME, FileUtils.getFilename(PATH, "\\\\"))
        assertEquals(FILENAME, FileUtils.getFilename(URL, "/"))
        assertEquals(FILENAME, FileUtils.getFilenameByUrl(URL))
    }

    @Test
    fun fileExt() {
        assertEquals(EXT, FileUtils.getFileExt(FileUtils.getFilename(PATH, "\\\\")))
        assertEquals(EXT, FileUtils.getFileExt(FileUtils.getFilename(URL, "/")))
    }

    companion object {
        const val PATH: String = "C:\\code\\FileUtils.java"
        const val URL: String = "file:///code/FileUtils.java"
        const val FILENAME: String = "FileUtils.java"
        const val EXT: String = "java"
    }
}
