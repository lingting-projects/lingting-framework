package live.lingting.framework.aws.s3

import com.fasterxml.jackson.annotation.JsonIgnore
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.Body
import live.lingting.framework.util.DurationUtils.hours
import java.time.Duration

/**
 * @author lingting 2025/6/3 20:01
 */
class AwsS3PreRequest(method: HttpMethod, body: Body? = null) : AwsS3SimpleRequest(method, body) {

    /**
     * 指定过期时长
     */
    @JsonIgnore
    var expire: Duration = 24.hours

    /**
     * 如果请求头存在 security-token(sts临时密钥). 该token是否参与签名
     */
    @JsonIgnore
    var tokenSigned = false

}
