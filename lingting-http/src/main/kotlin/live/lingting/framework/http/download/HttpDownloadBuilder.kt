package live.lingting.framework.http.download

import live.lingting.framework.download.DownloadBuilder
import live.lingting.framework.http.HttpClient
import java.net.URI
import java.time.Duration

/**
 * @author lingting 2023-12-20 16:49
 */
class HttpDownloadBuilder(url: String) : DownloadBuilder<HttpDownloadBuilder?>(url) {
    /**
     * 客户端配置
     */
    var client: HttpClient = DEFAULT_CLIENT

    constructor(url: URI) : this(url.toString())

    fun client(client: HttpClient): HttpDownloadBuilder {
        this.client = client
        return this
    }


    override fun build(): HttpDownload {
        return HttpDownload(this)
    }

    companion object {
        val DEFAULT_CLIENT: HttpClient = HttpClient.Companion.okhttp()
            .disableSsl()
            .callTimeout(Duration.ofSeconds(10))
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .build()
    }
}
