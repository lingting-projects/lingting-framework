package live.lingting.framework.system

import java.io.File
import java.io.OutputStream
import java.nio.charset.Charset
import java.time.Duration
import java.util.StringTokenizer
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.SystemUtils

/**
 * @author lingting 2022/6/25 11:55
 */
open class Command protected constructor(init: String, enter: String, exit: String, charset: Charset) {

    companion object {

        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("command")

        @JvmField
        val ENTER: String = SystemUtils.lineSeparator()

        const val EXIT: String = "exit"

        /**
         * 推荐使用此实例
         */
        /**
         * 获取命令操作实例. 此实例默认使用系统字符集, 如果发现部分带非英文字符和特殊符号命令执行异常, 建议使用
         * [Command.of] 自定义对应的字符集
         * @param init 初始命令
         */
        @JvmStatic
        open fun of(init: String): Command = of(init, SystemUtils.charset())

        @JvmStatic
        open fun of(init: String, charset: Charset): Command {
            return of(init, ENTER, EXIT, charset)
        }

        @JvmStatic
        open fun of(init: String, enter: String, exit: String, charset: Charset): Command {
            return Command(init, enter, exit, charset)
        }
    }

    val init: String

    protected val process: Process

    protected val stdIn: OutputStream

    /**
     * 标准输出
     */

    val stdOut: File

    val stdErr: File

    val enter: String

    val exit: String

    val charset: Charset

    val startTime: Long

    protected val history: MutableList<String> = ArrayList()

    init {
        require(StringUtils.hasText(init)) { "Empty init" }
        this.init = init
        val st = StringTokenizer(init)
        val array = arrayOfNulls<String>(st.countTokens())
        var i = 0
        while (st.hasMoreTokens()) {
            array[i] = st.nextToken()
            i++
        }

        this.stdOut = FileUtils.createTemp(".out", TEMP_DIR)
        this.stdErr = FileUtils.createTemp(".err", TEMP_DIR)

        // 重定向标准输出和标准错误到文件, 避免写入到缓冲区然后占满导致 waitFor 死锁
        val builder = ProcessBuilder(*array).redirectError(stdErr).redirectOutput(stdOut)
        this.process = builder.start()
        this.stdIn = process.outputStream
        this.enter = enter
        this.exit = exit
        this.charset = charset
        this.startTime = DateTime.millis()
    }

    open fun history(): List<String> {
        return history.toList()
    }

    open fun write(str: String): Command {
        val bytes: ByteArray = str.toByteArray(charset)
        stdIn.write(bytes)
        stdIn.flush()
        if (enter != str) {
            history.add(str)
        }
        return this
    }

    /**
     * 换到下一行
     */

    open fun enter(): Command {
        return write(enter)
    }

    /**
     * 写入通道退出指令
     */

    open fun exit(): Command {
        write(exit)
        return enter()
    }

    /**
     * 写入并执行一行指令
     * @param str 单行指令
     */

    open fun exec(str: String): Command {
        write(str)
        return enter()
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
    open fun waitFor(exit: Boolean = true, force: Boolean = false): CommandResult {
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
     * @return live.lingting.tools.system.CommandResult
     */
    @JvmOverloads
    open fun waitFor(duration: Duration?, exit: Boolean = true, force: Boolean = false): CommandResult {
        try {
            if (duration == null || !duration.isPositive) {
                val i = process.waitFor()
                return CommandResult(this, i)
            }

            // 超时
            if (!process.waitFor(duration.toMillis(), TimeUnit.MILLISECONDS)) {
                throw TimeoutException()
            }
            val i = process.exitValue()
            return CommandResult(this, i)
        } catch (e: TimeoutException) {
            if (exit) destroy(force)
            throw e
        } catch (e: InterruptedException) {
            if (exit) destroy(force)
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

    open fun clean() {
        FileUtils.delete(stdOut)
        FileUtils.delete(stdErr)
    }

    /**
     * 清空历史记录
     * @return 返回被清除的数据
     */
    open fun cleanHistory(): List<String> {
        val back: List<String> = ArrayList(history)
        history.clear()
        return back
    }

}
