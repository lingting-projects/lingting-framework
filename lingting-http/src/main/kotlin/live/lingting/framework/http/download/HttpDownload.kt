package live.lingting.framework.http.download

import live.lingting.framework.data.DataSize
import live.lingting.framework.download.MultipartDownload
import live.lingting.framework.exception.DownloadException
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.multipart.Part
import live.lingting.framework.util.DataSizeUtils.bytes
import java.io.InputStream
import java.net.URI

/**
 * @author lingting 2023-12-20 16:43
 */
open class HttpDownload(builder: HttpDownloadBuilder) : MultipartDownload<HttpDownload>(builder) {

    companion object {
        @JvmStatic
        fun builder(url: String): HttpDownloadBuilder {
            return HttpDownloadBuilder(url)
        }

        @JvmStatic
        fun single(url: String): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).single()
        }

        @JvmStatic
        fun multi(url: String): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).multi()
        }

        @JvmStatic
        fun builder(url: URI): HttpDownloadBuilder {
            return HttpDownloadBuilder(url)
        }

        @JvmStatic
        fun single(url: URI): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).single()
        }

        @JvmStatic
        fun multi(url: URI): HttpDownloadBuilder {
            return HttpDownloadBuilder(url).multi()
        }
    }

    protected val client: HttpClient = builder.client

    protected val uri: URI = URI.create(url)

    override fun size(): DataSize {
        val builder = HttpRequest.builder()
            .method(HttpMethod.HEAD)
            .url(uri)
            .header("Accept-Encoding", "identity")
        val response = client.request(builder.build())
        val headers = response.headers()
        return headers.contentLength().bytes
    }

    override fun download(part: Part): InputStream {
        val builder = HttpRequest.builder().get().url(uri)
        if (isMulti) {
            builder.header("Range", String.format("bytes=%d-%d", part.start.bytes, part.end.bytes))
        }

        val request = builder.build()
        val response = client.request(request)

        if (!response.is2xx) {
            throw DownloadException(String.format("response status: %d", response.code()))
        }

        return response.body()
    }

}
