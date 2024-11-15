package live.lingting.framework.system

import java.io.Closeable
import java.io.InputStream
import java.nio.file.Files
import java.time.Duration
import live.lingting.framework.util.StreamUtils

/**
 * @author lingting 2022/6/25 12:01
 */
class CommandResult(val command: Command, val exitCode: Int) : Closeable {
    val end: Long = System.currentTimeMillis()

    val duration: Duration = Duration.ofMillis(end - command.startTime)

    val stdOut: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        stdOut().use {
            StreamUtils.toString(it, command.charset)
        }
    }

    val stdErr: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        stdErr().use {
            StreamUtils.toString(it, command.charset)
        }
    }

    fun stdOut(): InputStream {
        return Files.newInputStream(command.stdOut.toPath())
    }

    fun stdErr(): InputStream {
        return Files.newInputStream(command.stdErr.toPath())
    }

    fun clean() {
        command.clean()
    }

    override fun close() {
        stdOut
        stdErr
        clean()
    }

}
