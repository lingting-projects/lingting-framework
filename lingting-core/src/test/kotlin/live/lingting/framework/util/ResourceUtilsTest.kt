package live.lingting.framework.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.InputStream
import java.util.function.Consumer

/**
 * @author lingting 2024-09-12 10:54
 */
internal class ResourceUtilsTest {
    @Test
    fun scan() {
        val s1 = ResourceUtils.scan(".") { r -> !r!!.isDirectory() && r.name.startsWith("s") }
        Assertions.assertEquals(3, s1.size)
        s1.forEach(Consumer { r: ResourceUtils.Resource -> Assertions.assertTrue(r.name.startsWith("s")) })

        val s2 = ResourceUtils.scan(".") { r -> !r!!.isDirectory() && r.name.endsWith(".sh") }
        Assertions.assertEquals(2, s2.size)
        s2.forEach(Consumer { r: ResourceUtils.Resource -> Assertions.assertTrue(r.name.endsWith(".sh")) })
        for (r in s2) {
            Assertions.assertDoesNotThrow<InputStream> { r.stream() }.use { stream ->
                val content = Assertions.assertDoesNotThrow<String> { StreamUtils.toString(stream) }
                val trim = content.trim { it <= ' ' }
                Assertions.assertEquals(r.name, trim)
            }
        }

        val s3 = ResourceUtils.scan(".", ResourceUtils.Resource::isDirectory)
        for (r in s3) {
            if (r.isFile) {
                val file = r.file()
                Assertions.assertTrue(file.isDirectory)
                Assertions.assertTrue(file.exists())
            }
        }

        val s4 = ResourceUtils.get("scripts/ss1.sh")
        Assertions.assertNotNull(s4)
        Assertions.assertEquals("ss1.sh", StreamUtils.toString(s4!!.stream()).trim { it <= ' ' })
        val s5 = ResourceUtils.get("scripts/ss9.sh")
        Assertions.assertNull(s5)
    }
}
