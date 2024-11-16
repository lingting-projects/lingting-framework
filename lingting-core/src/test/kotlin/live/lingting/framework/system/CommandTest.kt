package live.lingting.framework.system

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.SystemUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 16:41
 */
internal class CommandTest {
    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun test() {
        val result = if (SystemUtils.isWindows) {
            testWindows()
        } else {
            testLinux()
        }
        val command = result.command
        assertTrue(StringUtils.hasText(command.init))
        for (c in command.history()) {
            Assertions.assertNotEquals(command.enter, c)
        }
        val out = command.stdOut
        assertTrue(Files.size(out.toPath()) > 0)
        val stdOut = result.stdOut
        result.stdOut().use { stream ->
            val bytes = StreamUtils.read(stream)
            val string = String(bytes, StandardCharsets.UTF_8)
            println(string)
            assertEquals(stdOut, string)
        }
    }

    @Throws(InterruptedException::class, IOException::class)
    fun testLinux(): CommandResult {
        val command = Command.of("sh", StandardCharsets.UTF_8)
        command.exec("ls")
        command.exit()
        return command.waitFor()
    }

    @Throws(IOException::class, InterruptedException::class)
    fun testWindows(): CommandResult {
        val command = Command.of("cmd", StandardCharsets.UTF_8)
        command.exec("dir")
        command.exit()
        return command.waitFor()
    }
}
