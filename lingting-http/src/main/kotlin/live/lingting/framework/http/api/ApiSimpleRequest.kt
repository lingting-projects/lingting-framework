package live.lingting.framework.http.api

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.BodySource

/**
 * @author lingting 2024-09-19 15:41
 */
open class ApiSimpleRequest @JvmOverloads constructor(
    protected val method: HttpMethod,
    protected val path: String,
    protected val body: BodySource = BodySource.empty()
) : ApiRequest() {
    override fun method(): HttpMethod {
        return method
    }

    override fun path(): String {
        return path
    }

    override fun body(): BodySource {
        return body
    }
}
