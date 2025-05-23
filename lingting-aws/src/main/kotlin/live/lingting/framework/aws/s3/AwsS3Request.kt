package live.lingting.framework.aws.s3

import com.fasterxml.jackson.annotation.JsonIgnore
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.header.HttpHeaders
import java.time.Duration

/**
 * @author lingting 2024-09-19 15:03
 */
abstract class AwsS3Request : ApiRequest() {

    /**
     * 如果是预签名操作, 则指定过期时长
     */
    @JsonIgnore
    var expire: Duration? = null

    var key: String = ""

    var acl: Acl? = null

    /**
     * 自定义元数据key(不要前缀, 自动拼)
     */
    val meta: HttpHeaders = HttpHeaders.empty()

    override fun path(): String {
        return key
    }

    fun setAclIfAbsent(acl: Acl) {
        val method = method()
        if (this.acl == null && method.allowBody()) {
            this.acl = acl
        }
    }
}
