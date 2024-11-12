package live.lingting.framework.http.download

import live.lingting.framework.download.MultipartDownload
import live.lingting.framework.exception.DownloadException
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.multipart.Part
import live.lingting.framework.util.StreamUtils
import okhttp3.Cookie.Builder.value
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URI

/**
 * @author lingting 2023-12-20 16:43
 */
class HttpDownload(builder: HttpDownloadBuilder) : MultipartDownload<HttpDownload?>(builder) {
    protected val client: HttpClient? = builder.client

    protected val uri: URI = URI.create(url)

    @Throws(IOException::class)
    fun write(request: HttpRequest, output: OutputStream?) {
        val response = client!!.request(request)

        if (!response!!.is2xx) {
            throw DownloadException(String.format("response status: %d", response.code()))
        }

        response.body().use { input ->
            StreamUtils.write(input, output!!)
        }
    }

    @Throws(IOException::class)
    override fun size(): Long {
        val builder: HttpRequest.Builder = HttpRequest.Companion.builder().url(uri).header("Accept-Encoding", "identity")
        val response = client!!.request(builder.build())
        val headers = response!!.headers()
        return headers!!.contentLength()
    }

    @Throws(Exception::class)
    override fun download(part: Part): InputStream {
        val builder: HttpRequest.Builder = HttpRequest.Companion.builder().get().url(uri)
        if (isMulti) {
            builder.header("Range", String.format("bytes=%d-%d", part.start, part.end))
        }

        val request = builder.build()
        val response = client!!.request(request)

        if (!response!!.is2xx) {
            throw DownloadException(String.format("response status: %d", response.code()))
        }

        return response.body()
    }

    companion object {
        fun builder(url: String): HttpDownloadBuilder {
            return HttpDownloadBuilder(url)
        }

        @JvmStatic
        fun single(url: String): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).single()!!
        }

        fun multi(url: String): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).multi()!!
        }

        fun builder(url: URI): HttpDownloadBuilder {
            return HttpDownloadBuilder(url)
        }

        @JvmStatic
        fun single(url: URI): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).single()!!
        }

        @JvmStatic
        fun multi(url: URI): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).multi()!!
        }
    }
}
