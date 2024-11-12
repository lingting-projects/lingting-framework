package live.lingting.framework.http.api

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.BodySource

/**
 * @author lingting 2024-09-19 15:41
 */
class ApiSimpleRequest @JvmOverloads constructor(protected val method: HttpMethod, protected val uri: String, protected val body: BodySource? = null) : ApiRequest() {
    override fun method(): HttpMethod {
        return method
    }

    override fun path(): String {
        return uri
    }

    override fun body(): BodySource? {
        return body
    }
}
