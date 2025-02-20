package live.lingting.framework.system

import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.nio.charset.Charset
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.ValueUtils

/**
 * @author lingting 2025/2/19 20:25
 */
class CommandBuilder(init: String) {

    init {
        check(init.isNotBlank()) { "init is blank" }
    }

    private var init: String = init.trim()

    private var enter: ByteArray = Command.ENTER_BYTES

    private var charset: Charset = Command.CHARSET

    private var exit: ByteArray = Command.EXIT_BYTES

    private var history: Boolean = false

    private var redirectOut: Redirect = Redirect.PIPE

    private var redirectErr: Redirect = Redirect.PIPE

    fun enter(enter: ByteArray): CommandBuilder {
        this.enter = enter
        return this
    }

    fun charset(charset: Charset): CommandBuilder {
        this.charset = charset
        return this
    }

    fun exit(exit: ByteArray): CommandBuilder {
        this.exit = exit
        return this
    }

    fun history(history: Boolean): CommandBuilder {
        this.history = history
        return this
    }

    fun history(): CommandBuilder {
        return history(true)
    }

    fun redirectOut(redirectOut: Redirect): CommandBuilder {
        this.redirectOut = redirectOut
        return this
    }

    fun redirectErr(redirectErr: Redirect): CommandBuilder {
        this.redirectErr = redirectErr
        return this
    }

    fun buildIgnore() = build(Redirect.DISCARD, Redirect.DISCARD)

    fun buildTemp() = ValueUtils.simpleUuid().let {
        val out = FileUtils.createFile("$it.out", Command.TEMP_DIR)
        val err = FileUtils.createFile("$it.err", Command.TEMP_DIR)
        buildRedirect(out, err)
    }

    fun buildRedirect(out: File, err: File) = build(Redirect.to(out), Redirect.to(err))

    fun build(): Command = build(redirectOut, redirectErr)

    fun build(out: Redirect, err: Redirect): Command {
        if (history) {
            return HistoryCommand(init, enter, exit, charset, out, err)
        }
        return Command(init, enter, exit, charset, out, err)
    }

}
