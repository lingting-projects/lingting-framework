package live.lingting.framework.ali

import live.lingting.framework.ali.properties.AliProperties
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.util.HttpUtils

/**
 * @author lingting 2024-09-14 13:49
 */
abstract class AliClient<R : AliRequest?> protected constructor(properties: AliProperties) : ApiClient<R>(properties.host()) {
    protected val ak: String? = properties.ak

    protected val sk: String? = properties.sk

    protected val token: String? = properties.token

    companion object {
        protected val HEADER_INCLUDE: Array<String> = arrayOf(HttpUtils.HEADER_HOST, "content-type", "content-md5")
    }
}
