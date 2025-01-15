package live.lingting.framework.aws.s3.request

import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.util.BooleanUtils.ifTrue

/**
 * @author lingting 2025/1/14 19:57
 */
class AwsS3ListObjectRequest : AwsS3Request() {

    var v2: Boolean = true

    var prefix: String? = null

    /**
     * v1: marker
     * v2: continuationToken
     */
    var token: String? = null

    var delimiter: String? = null

    /**
     * 最大 1000
     */
    var maxKeys: Int = 1000

    override fun method(): HttpMethod {
        return HttpMethod.GET
    }

    override fun onParams() {
        if (v2) {
            params.add("list-type", "2")
        }
        prefix?.run { isNotBlank().ifTrue { params.add("prefix", this) } }
        token?.run {
            isNotBlank().ifTrue {
                if (v2) {
                    params.add("continuation-token", this)
                } else {
                    params.add("marker", this)
                }
            }
        }
        delimiter?.run { isNotBlank().ifTrue { params.add("delimiter", this) } }
        params.add("max-keys", maxKeys.toString())
    }

    fun copy(): AwsS3ListObjectRequest {
        val request = AwsS3ListObjectRequest()
        request.v2 = v2
        request.prefix = prefix
        request.token = token
        request.delimiter = delimiter
        request.maxKeys = maxKeys
        return request
    }

}
