package live.lingting.framework.ali

import live.lingting.framework.ali.properties.AliProperties
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.header.HttpHeaderKeys

/**
 * @author lingting 2024-09-14 13:49
 */
abstract class AliClient<R : AliRequest> protected constructor(properties: AliProperties) : ApiClient<R>(properties.host()) {
    protected val ak: String = properties.ak

    protected val sk: String = properties.sk

    protected val token: String? = properties.token

    companion object {
        @JvmField
        protected val HEADER_INCLUDE: Array<String> = arrayOf(HttpHeaderKeys.HOST, "content-type", "content-md5")
    }
}
