package live.lingting.framework.system

import live.lingting.framework.util.StreamUtils
import java.io.Closeable
import java.io.InputStream
import java.nio.file.Files
import java.time.Duration

/**
 * @author lingting 2022/6/25 12:01
 */
class CommandResult(val command: Command, val exitCode: Int) : Closeable {
    val end: Long = System.currentTimeMillis()

    val duration: Duration = Duration.ofMillis(end - command.startTime)

    protected var stdOut: String? = null

    protected var stdErr: String? = null


    @kotlin.jvm.Synchronized
    fun getStdOut(): String {
        if (stdOut == null) {
            val stream = stdOut()
            stdOut = String(StreamUtils.read(stream), command.charset)
        }
        return stdOut!!
    }


    @kotlin.jvm.Synchronized
    fun getStdErr(): String {
        if (stdErr == null) {
            val stream = stdErr()
            stdErr = String(StreamUtils.read(stream), command.charset)
        }
        return stdErr!!
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
        getStdOut()
        getStdErr()
        clean()
    }
}
