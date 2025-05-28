package live.lingting.framework.huawei

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.api.ApiRequest

/**
 * @author lingting 2024-09-12 21:43
 */
abstract class HuaweiRequest : ApiRequest() {

    override fun method(): HttpMethod {
        return HttpMethod.POST
    }

    override fun onCall() {
        headers.contentType("application/json;charset=utf8")
    }

}
