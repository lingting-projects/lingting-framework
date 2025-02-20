package live.lingting.framework.system

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import live.lingting.framework.concurrent.Await
import live.lingting.framework.function.StateKeepRunnable
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.ThreadUtils

/**
 * @author lingting 2025/2/20 15:40
 */
class CommandPipe(
    val pid: Long,
    private val redirect: ProcessBuilder.Redirect,
    private val stream: InputStream,
    val charset: Charset
) : Closeable {

    companion object {

        val log = logger()

    }

    private val file: File? = redirect.file()

    private var bytes: ByteArray = byteArrayOf()

    private val r = object : StateKeepRunnable("cPipe-$pid") {
        override fun doProcess() {
            if (redirect.type() != ProcessBuilder.Redirect.Type.PIPE) {
                return
            }

            try {
                val arrayStream = ByteArrayOutputStream()
                arrayStream.use { out ->
                    val temp = ByteArray(StreamUtils.readSize)
                    var len: Int
                    while (true) {
                        len = stream.read(temp)
                        if (len < 1) {
                            break
                        }
                        out.write(temp, 0, len)
                    }
                }
                bytes = arrayStream.toByteArray()
            } catch (e: Exception) {
                log.error("command pipe read error!", e)
            }
        }

    }

    init {
        ThreadUtils.execute(r)
    }

    fun stream(): InputStream {
        Await.waitTrue { r.isFinish }
        if (file != null && FileUtils.NULL != file) {
            return file.inputStream()
        }
        return ByteArrayInputStream(bytes)
    }

    override fun close() {
        if (redirect != ProcessBuilder.Redirect.DISCARD) {
            FileUtils.delete(redirect.file())
        }
    }

}
