package live.lingting.framework.system

import live.lingting.framework.util.DurationUtils.seconds
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.SystemUtils
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier
import kotlin.test.assertEquals

/**
 * @author lingting 2024-01-26 16:41
 */
@Suppress("UNCHECKED_CAST")
class ProcessStreamTest {

    fun stream(): ProcessStream {
        val init: String
        val exec: String
        if (SystemUtils.isWindows) {
            init = "cmd"
            exec = "dir"
        } else {
            init = "sh"
            exec = "ls"
        }

        return ProcessStream.builder(init)
            .charset(SystemUtils.charset())
            .build()
            .also { it.exec(exec) }
    }

    @Test
    fun test() {
        val stream = stream()
        stream.exit()
        val code = assertDoesNotThrow(ThrowingSupplier { stream.waitFor(1.seconds) })
        assertTrue(stream.init.isNotBlank())
        assertEquals(code, stream.exitCode)
        val out = stream.outStream().use { StreamUtils.toString(it, stream.charset) }
        print(out)
        assertTrue(out.isNotBlank())
    }

}
