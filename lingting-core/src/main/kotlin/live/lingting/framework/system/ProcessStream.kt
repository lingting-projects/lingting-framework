package live.lingting.framework.system

import live.lingting.framework.stream.AsyncCopyInputStream
import live.lingting.framework.stream.BytesInputStream
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.SystemUtils
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.LocalDateTime
import java.util.StringTokenizer
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author lingting 2025/4/10 20:53
 */
open class ProcessStream(
    val init: String,
    val enter: ByteArray,
    val exit: ByteArray,
    val charset: Charset,
    private val redirectOut: ProcessBuilder.Redirect,
    private val redirectErr: ProcessBuilder.Redirect,
) : AutoCloseable {

    companion object {

        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("command")

        @JvmField
        val ENTER: String = SystemUtils.lineSeparator()

        @JvmField
        val ENTER_BYTES = ENTER.toByteArray()

        const val EXIT: String = "exit"

        @JvmField
        val EXIT_BYTES = EXIT.toByteArray()

        @JvmField
        val CHARSET: Charset = StandardCharsets.UTF_8

        @JvmStatic
        fun builder(init: String): ProcessStreamBuilder {
            return ProcessStreamBuilder(init)
        }

        @JvmStatic
        fun of(init: String): ProcessStream = builder(init).build()

        @JvmStatic
        fun of(init: String, charset: Charset): ProcessStream = builder(init).charset(charset).build()

        @JvmStatic
        fun of(init: String, enter: String, exit: String, charset: Charset): ProcessStream =
            of(init, enter.toByteArray(charset), exit.toByteArray(charset), charset)

        @JvmStatic
        fun of(init: String, enter: ByteArray, exit: ByteArray, charset: Charset): ProcessStream =
            builder(init).enter(enter).exit(exit).charset(charset).build()

    }

    protected val process: Process

    val pid: Long

    protected val stdIn: OutputStream

    val startTime: LocalDateTime

    /**
     * null 表示未结束
     */
    val exitCode: Int?
        get() = try {
            process.exitValue()
        } catch (_: IllegalThreadStateException) {
            null
        }

    protected var out: InputStream? = null
    protected var err: InputStream? = null

    init {
        check(init.isNotBlank()) { "init can not be blank" }
        val builder = builder()
        onBuild(builder)
        process = builder.start()
        pid = process.pid()
        startTime = LocalDateTime.now()
        stdIn = process.outputStream
        if (redirectOut.file() == null) {
            out = if (redirectOut.type() != ProcessBuilder.Redirect.Type.PIPE) {
                BytesInputStream(byteArrayOf())
            } else {
                AsyncCopyInputStream(process.inputStream, charset)
            }
        }
        if (redirectErr.file() == null) {
            err = if (redirectErr.type() != ProcessBuilder.Redirect.Type.PIPE) {
                BytesInputStream(byteArrayOf())
            } else {
                AsyncCopyInputStream(process.errorStream, charset)
            }
        }

    }

    protected open fun builder(): ProcessBuilder {
        val st = StringTokenizer(init)
        val array = arrayOfNulls<String>(st.countTokens())
        var i = 0
        while (st.hasMoreTokens()) {
            array[i] = st.nextToken()
            i++
        }
        return ProcessBuilder(*array)
            .redirectOutput(redirectOut)
            .redirectError(redirectErr)
    }

    protected open fun onBuild(builder: ProcessBuilder) {
        //
    }

    open fun write(str: String) {
        val bytes = str.toByteArray(charset)
        write(bytes)
    }

    open fun write(bytes: ByteArray) {
        stdIn.write(bytes)
        stdIn.flush()
    }

    /**
     * 换到下一行
     */
    open fun enter() {
        write(enter)
    }

    /**
     * 写入通道退出指令
     */
    open fun exit() {
        write(exit)
        enter()
    }

    /**
     * 写入并执行一行指令
     * @param str 单行指令
     */
    open fun exec(str: String) {
        write(str)
        enter()
    }

    open fun exec(bytes: ByteArray) {
        write(bytes)
        enter()
    }

    /**
     * 获取执行结果, 并退出
     * 注意: 如果套娃了多个通道, 则需要手动退出套娃的通道
     * 例如: eg: exec("ssh ssh.lingting.live").exec("ssh ssh.lingting.live").exec("ssh
     * ssh.lingting.live")
     * 需要: eg: exit().exit().exit()
     *
     * @param exit 是否在线程被中断后退出
     * @param force 是否强制退出
     */
    @JvmOverloads
    open fun waitFor(exit: Boolean = true, force: Boolean = false): Int {
        return waitFor(null, exit, force)
    }

    /**
     * 等待命令执行完成
     * <h3>如果 process 是通过 [Runtime.exec]方法构建的, 那么[Process.waitFor]方法可能会导致线程卡死,
     * 具体原因如下</h3>
     * 终端缓冲区大小有限, 在缓冲区被写满之后, 会子线程会挂起,等待缓冲区内容被读, 然后才继续写. 如果此时主线程也在waitFor()等待子线程结束, 就卡死了
     * 即便是先读取返回结果在调用此方法也可能会导致卡死. 比如: 先读取标准输出流, 还没读完, 缓冲区被错误输出流写满了.
     * @param duration 等待时长
     * @param exit 是否在线程被中断或超时后退出
     * @param force 是否强制退出
     * @return live.lingting.framework.system.CommandResult
     */
    @JvmOverloads
    open fun waitFor(duration: Duration?, exit: Boolean = true, force: Boolean = false): Int {
        try {
            if (duration == null || !duration.isPositive) {
                return process.waitFor()
            }
            // 超时
            if (!process.waitFor(duration.toMillis(), TimeUnit.MILLISECONDS)) {
                throw TimeoutException()
            }
            return process.exitValue()
        } catch (e: TimeoutException) {
            if (exit) destroy(force)
            throw e
        } catch (e: InterruptedException) {
            if (exit) destroy(force)
            Thread.currentThread().interrupt()
            throw e
        }
    }

    @JvmOverloads
    open fun destroy(force: Boolean = false) {
        if (force) {
            process.destroyForcibly()
        } else {
            process.destroy()
        }
    }

    open fun destroyForcibly() = destroy(true)

    @Synchronized
    fun outStream(): InputStream {
        val file = redirectOut.file()
        if (file != null) {
            return file.inputStream()
        }
        return out!!
    }

    @JvmOverloads
    fun outString(charset: Charset = this.charset) = outStream().use { StreamUtils.toString(it, charset) }

    @Synchronized
    fun errStream(): InputStream {
        val file = redirectErr.file()
        if (file != null) {
            return file.inputStream()
        }
        return err!!
    }

    @JvmOverloads
    fun errString(charset: Charset = this.charset) = errStream().use { StreamUtils.toString(it, charset) }

    override fun close() {
        if (process.isAlive) {
            destroyForcibly()
        }
    }

}
