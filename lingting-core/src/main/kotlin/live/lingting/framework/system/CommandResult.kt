package live.lingting.framework.system

import java.io.Closeable
import java.io.InputStream
import java.nio.charset.Charset
import java.time.LocalDateTime
import live.lingting.framework.time.DateTime

/**
 * @author lingting 2025/2/20 13:52
 */
open class CommandResult @JvmOverloads constructor(
    protected val command: Command,
    val pid: Long,
    val startTime: LocalDateTime = command.startTime,
    val endTime: LocalDateTime = DateTime.current(),
    val code: Int,
    val charset: Charset = command.charset,
    private val outPipe: CommandPipe,
    private val errPipe: CommandPipe,
) : Closeable {

    open fun outStream(): InputStream = outPipe.stream()

    open fun errStream(): InputStream = errPipe.stream()

    open fun outBytes(): ByteArray = outStream().use { it.readAllBytes() }

    open fun errBytes(): ByteArray = errStream().use { it.readAllBytes() }

    open fun outString(): String = outBytes().toString(charset)

    open fun errString(): String = errBytes().toString(charset)

    override fun close() {
        command.close()
    }

}
