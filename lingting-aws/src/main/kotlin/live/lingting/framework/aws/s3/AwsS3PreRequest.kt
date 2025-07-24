package live.lingting.framework.aws.s3

import com.fasterxml.jackson.annotation.JsonIgnore
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.body.Body
import live.lingting.framework.multipart.Part
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

    fun multipart(uploadId: String?, part: Part?) {
        AwsS3ObjectPutRequest.addMultipartParams(params, uploadId, part)
    }

}
