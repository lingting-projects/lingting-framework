package live.lingting.framework.http.download

import java.net.URI
import live.lingting.framework.download.DownloadBuilder
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.api.ApiClient

/**
 * @author lingting 2023-12-20 16:49
 */
class HttpDownloadBuilder(url: String) : DownloadBuilder<HttpDownloadBuilder>(url) {

    /**
     * 客户端配置
     */
    var client = ApiClient.defaultClient

    constructor(url: URI) : this(url.toString())

    fun client(client: HttpClient): HttpDownloadBuilder {
        this.client = client
        return this
    }

    override fun build(): HttpDownload {
        return HttpDownload(this)
    }

}
