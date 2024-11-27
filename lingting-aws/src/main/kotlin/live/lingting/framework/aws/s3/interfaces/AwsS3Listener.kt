package live.lingting.framework.aws.s3.interfaces


import java.time.LocalDateTime
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2024/11/5 14:47
 */
interface AwsS3Listener {
    fun onFailed(request: AwsS3Request, response: HttpResponse)

    fun onAuthorization(request: AwsS3Request, headers: HttpHeaders, params: StringMultiValue, now: LocalDateTime)
}
