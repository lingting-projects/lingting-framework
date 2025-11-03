package live.lingting.framework.util

import live.lingting.framework.resource.FileResourceResolver
import live.lingting.framework.resource.JarResourceResolver
import live.lingting.framework.resource.Resource
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.io.InputStream
import java.util.function.Consumer

/**
 * @author lingting 2024-09-12 10:54
 */
class ResourceUtilsTest {

    @Test
    fun scan() {
        val s1 = ResourceUtils.scan(".") { r -> !r.isDirectory && r.name.startsWith("s") }
        assertEquals(3, s1.size)
        s1.forEach(Consumer { assertTrue(it.name.startsWith("s")) })

        val s2 = ResourceUtils.scan(".") { r -> !r.isDirectory && r.name.endsWith(".sh") }
        assertEquals(2, s2.size)
        s2.forEach { assertTrue(it.name.endsWith(".sh")) }
        for (r in s2) {
            assertDoesNotThrow<InputStream> {
                r.stream()
            }.use { stream ->
                val content = assertDoesNotThrow<String> { StreamUtils.toString(stream) }
                val trim = content.trim()
                assertEquals(r.name, trim)
            }
        }

        val s3 = ResourceUtils.scan(".") { it.isDirectory }
        for (r in s3) {
            if (r.protocol.startsWith(FileResourceResolver.PROTOCOL)) {
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
        val s6 = ResourceUtils.scan("jakarta/annotation")
        assertNotNull(s6)
        s6.forEach {
            assertTrue(it.protocol.startsWith(JarResourceResolver.PROTOCOL))
            assertFalse(it.name.contains("/"))
        }
        assertTrue(s6.any { it.name.endsWith(".class") })
    }

    @Test
    fun instance() {
        val f1 = SystemUtils.homeDir()
        val r1 = Resource(FileResourceResolver.PROTOCOL, f1)
        assertDoesNotThrow { r1.url }
        val r2 = Resource(r1.protocol, "/.andoird", ".android", f1.absolutePath, true)
        assertDoesNotThrow { r2.url }
        val r3 = Resource(r2.protocol, "/adbkey", "adbkey", r2.root + r2.path, false)
        assertDoesNotThrow { r3.url }
        val r4 = Resource(r2.protocol, File(r2.file(), "adbkey.pub"))
        assertDoesNotThrow { r4.url }
    }

}
