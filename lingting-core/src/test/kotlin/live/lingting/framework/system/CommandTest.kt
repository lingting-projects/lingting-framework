package live.lingting.framework.system

import live.lingting.framework.util.DurationUtils.seconds
import live.lingting.framework.util.SystemUtils
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

/**
 * @author lingting 2024-01-26 16:41
 */
@Suppress("UNCHECKED_CAST")
class CommandTest {

    fun command(): Command {
        val init: String
        val exec: String
        if (SystemUtils.isWindows) {
            init = "cmd"
            exec = "dir"
        } else {
            init = "sh"
            exec = "ls"
        }

        return Command.builder(init)
            .history()
            .charset(SystemUtils.charset())
            .build()
            .also { it.exec(exec) }
    }

    @Test
    fun test() {
        val command = command() as HistoryCommand
        command.exit()
        val result = assertDoesNotThrow(ThrowingSupplier { command.waitFor(1.seconds) })
        assertTrue(command.init.isNotBlank())
        for (c in command.history()) {
            assertNotEquals(command.enter, c)
        }
        val out = result.outString()
        print(out)
        assertTrue(out.isNotBlank())
    }

}
