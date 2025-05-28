package live.lingting.framework.http.api

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.Body

/**
 * @author lingting 2024-09-19 15:41
 */
open class ApiSimpleRequest @JvmOverloads constructor(
    protected val method: HttpMethod,
    protected val path: String,
    protected val body: Body = Body.empty()
) : ApiRequest() {
    override fun method(): HttpMethod {
        return method
    }

    override fun path(): String {
        return path
    }

    override fun body(): Body {
        return body
    }
}
