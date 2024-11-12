package live.lingting.framework.http.java

import live.lingting.framework.flow.FutureSubscriber
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils
import okhttp3.Cookie.Builder.value
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.http.HttpRequest.BodyPublisher
import java.nio.ByteBuffer

/**
 * @author lingting 2024-09-14 17:47
 */
class JavaHttpUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        fun write(publisher: BodyPublisher): File {
            val subscriber: FutureSubscriber<File, ByteBuffer> = object : FutureSubscriber<File?, ByteBuffer?>() {
                override fun convert(list: List<ByteBuffer>): File {
                    val file = FileUtils.createTemp(".http")

                    FileOutputStream(file).use { out ->
                        for (buffer in list) {
                            val remaining = buffer.remaining()
                            val bytes = ByteArray(remaining)
                            buffer[bytes]
                            out.write(bytes)
                        }
                    }
                    return file
                }
            }

            publisher.subscribe(subscriber)
            return subscriber.get()
        }

        @Throws(IOException::class)
        fun toString(publisher: BodyPublisher): String {
            val file = write(publisher)
            FileInputStream(file).use { stream ->
                return StreamUtils.toString(stream)
            }
        }
    }
}
