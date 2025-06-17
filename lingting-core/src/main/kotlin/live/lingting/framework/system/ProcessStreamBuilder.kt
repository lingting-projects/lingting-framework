package live.lingting.framework.system

import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.ValueUtils
import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.nio.charset.Charset

/**
 * @author lingting 2025/4/10 20:54
 */
class ProcessStreamBuilder(init: String) {

    init {
        check(init.isNotBlank()) { "init is blank" }
    }

    private var init: String = init.trim()

    private var enter: ByteArray = ProcessStream.ENTER_BYTES

    private var charset: Charset = ProcessStream.CHARSET

    private var exit: ByteArray = ProcessStream.EXIT_BYTES

    private var redirectOut: Redirect = Redirect.PIPE

    private var redirectErr: Redirect = Redirect.PIPE

    fun enter(enter: ByteArray): ProcessStreamBuilder {
        this.enter = enter
        return this
    }

    fun charset(charset: Charset): ProcessStreamBuilder {
        this.charset = charset
        return this
    }

    fun exit(exit: ByteArray): ProcessStreamBuilder {
        this.exit = exit
        return this
    }

    fun redirectOut(redirectOut: Redirect): ProcessStreamBuilder {
        this.redirectOut = redirectOut
        return this
    }

    fun redirectErr(redirectErr: Redirect): ProcessStreamBuilder {
        this.redirectErr = redirectErr
        return this
    }

    fun redirectIgnore(): ProcessStreamBuilder {
        return redirectFile(FileUtils.NULL, FileUtils.NULL)
    }

    fun redirectFile(out: File, err: File): ProcessStreamBuilder {
        redirectOut = Redirect.to(out)
        redirectErr = Redirect.to(err)
        return this
    }

    fun redirectTemp() = ValueUtils.simpleUuid().let {
        val out = FileUtils.createFile("$it.out", ProcessStream.TEMP_DIR)
        val err = FileUtils.createFile("$it.err", ProcessStream.TEMP_DIR)
        redirectFile(out, err)
    }

    fun build(): ProcessStream {
        return ProcessStream(init, enter, exit, charset, redirectOut, redirectErr)
    }


}
