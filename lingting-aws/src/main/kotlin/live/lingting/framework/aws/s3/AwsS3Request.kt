package live.lingting.framework.aws.s3

import com.fasterxml.jackson.annotation.JsonIgnore
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.header.HttpHeaders

/**
 * @author lingting 2024-09-19 15:03
 */
abstract class AwsS3Request : ApiRequest() {

    @JsonIgnore
    var key: String = ""

    @JsonIgnore
    var acl: Acl? = null

    /**
     * 自定义元数据key(不要前缀, 自动拼)
     */
    @JsonIgnore
    val meta: HttpHeaders = HttpHeaders.empty()

    override fun path(): String {
        return key
    }

}
