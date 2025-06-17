package live.lingting.framework.http.okhttp

import live.lingting.framework.util.StringUtils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException
import java.io.InputStream

/**
 * @author lingting 2024-09-28 14:11
 */
object OkHttpUtils {

    @JvmStatic
    fun input(body: RequestBody?): InputStream? {
        if (body == null) {
            return null
        }
        val buffer = Buffer()
        body.writeTo(buffer)
        return buffer.inputStream()
    }

    @JvmStatic
    fun bytes(body: RequestBody?): ByteArray {
        if (body == null) {
            return ByteArray(0)
        }
        val buffer = Buffer()
        try {
            body.writeTo(buffer)
        } catch (e: IOException) {
            return ByteArray(0)
        }
        return buffer.readByteArray()
    }

    @JvmStatic
    fun mediaType(body: live.lingting.framework.http.body.RequestBody): MediaType? {
        return mediaType((body.contentType()))
    }

    @JvmStatic
    fun mediaType(contentType: String?): MediaType? {
        return if (StringUtils.hasText(contentType)) contentType?.toMediaTypeOrNull() else null
    }

}

