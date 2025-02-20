package live.lingting.framework.system

import java.nio.charset.Charset

/**
 * @author lingting 2025/2/20 11:44
 */
open class HistoryCommand(
    init: String,
    enter: ByteArray,
    exit: ByteArray,
    charset: Charset,
    redirectOut: ProcessBuilder.Redirect,
    redirectErr: ProcessBuilder.Redirect,
) : Command(init, enter, exit, charset, redirectOut, redirectErr) {

    protected val enterString = String(enter, charset)

    protected val history: MutableList<String> = ArrayList()

    open fun history(): List<String> {
        return history.toList()
    }

    override fun write(str: String) {
        val bytes = str.toByteArray(charset)
        super.write(bytes)
        if (enterString != str) {
            history.add(str)
        }
    }

    override fun write(bytes: ByteArray) {
        val string = String(bytes, charset)
        super.write(bytes)
        if (enterString != string) {
            history.add(string)
        }
    }

}
