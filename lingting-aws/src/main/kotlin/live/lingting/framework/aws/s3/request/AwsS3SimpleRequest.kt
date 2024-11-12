package live.lingting.framework.aws.s3.request

import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.http.HttpMethod

/**
 * @author lingting 2024-09-19 19:22
 */
class AwsS3SimpleRequest(protected val method: HttpMethod) : AwsS3Request() {
    override fun method(): HttpMethod {
        return method
    }
}
