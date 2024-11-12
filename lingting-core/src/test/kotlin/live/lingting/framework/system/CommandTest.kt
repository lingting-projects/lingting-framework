package live.lingting.framework.system

import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.SystemUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files

/**
 * @author lingting 2024-01-26 16:41
 */
internal class CommandTest {
    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun test() {
        val result = if (SystemUtils.isWindows()) {
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
        Assertions.assertTrue(Files.size(out.toPath()) > 0)
        val stdOut = result.getStdOut()
        result.stdOut().use { stream ->
            val bytes = StreamUtils.read(stream)
            val string = String(bytes, StandardCharsets.UTF_8)
            println(string)
            Assertions.assertEquals(stdOut, string)
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
