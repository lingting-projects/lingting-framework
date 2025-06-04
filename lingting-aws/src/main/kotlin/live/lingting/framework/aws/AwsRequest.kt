package live.lingting.framework.aws

import live.lingting.framework.aws.properties.AwsProperties
import live.lingting.framework.http.HttpContentTypes
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.body.Body

/**
 * @author lingting 2025/6/3 14:45
 */
abstract class AwsRequest : ApiRequest() {

    override fun method(): HttpMethod = HttpMethod.POST

    override fun path(): String = ""

    abstract fun version(): String

    abstract fun action(): String

    open fun region() = AwsProperties.REGION

    override fun onCall() {
        headers.contentType(HttpContentTypes.FORM_URL_ENCODE)
    }

    override fun body(): Body {
        return Body.empty()
    }

}
