package live.lingting.framework.ali

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.api.ApiRequest

/**
 * @author lingting 2024-09-14 13:49
 */
abstract class AliRequest : ApiRequest() {
    override fun method(): HttpMethod {
        return HttpMethod.POST
    }

    override fun onCall() {
        headers.contentType("application/json;charset=utf8")
    }
}
