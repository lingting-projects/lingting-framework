package live.lingting.framework.util

import java.io.InputStream
import java.util.function.Consumer
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-12 10:54
 */
internal class ResourceUtilsTest {
    @Test
    fun scan() {
        val s1 = ResourceUtils.scan(".") { r -> !r.isDirectory && r.name.startsWith("s") }
        assertEquals(3, s1.size)
        s1.forEach(Consumer { assertTrue(it.name.startsWith("s")) })

        val s2 = ResourceUtils.scan(".") { r -> !r.isDirectory && r.name.endsWith(".sh") }
        assertEquals(2, s2.size)
        s2.forEach(Consumer { assertTrue(it.name.endsWith(".sh")) })
        for (r in s2) {
            assertDoesNotThrow<InputStream> { r.stream() }.use { stream ->
                val content = assertDoesNotThrow<String> { StreamUtils.toString(stream) }
                val trim = content.trim()
                assertEquals(r.name, trim)
            }
        }

        val s3 = ResourceUtils.scan(".", { it.isDirectory })
        for (r in s3) {
            if (r.fromFile) {
                val file = r.file()
                assertTrue(file.isDirectory)
                assertTrue(file.exists())
            }
        }

        val s4 = ResourceUtils.get("scripts/ss1.sh")
        assertNotNull(s4)
        assertEquals("ss1.sh", StreamUtils.toString(s4!!.stream()).trim())
        val s5 = ResourceUtils.get("scripts/ss9.sh")
        assertNull(s5)
    }
}
