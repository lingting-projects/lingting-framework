package live.lingting.framework.aws

import live.lingting.framework.aws.AwsUtils.parse
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.value.multi.StringMultiValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-19 20:37
 */
class AwsSignV4Test {

    @Test
    fun test() {
        val headers = HttpHeaders.empty()
        headers.put("Host", "examplebucket.s3.amazonaws.com")
        headers.put("Range", "bytes=0-9")
        headers.put("x-amz-content-sha256", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
        headers.put("x-amz-date", "20130524T000000Z")

        val bodySha256 = headers.first(AwsUtils.HEADER_CONTENT_SHA256)!!
        val dateTime = parse(headers.first(AwsUtils.HEADER_DATE)!!, AwsSignV4.DATETIME_FORMATTER)

        val params = StringMultiValue()

        val sing = AwsSignV4.builder()
            .dateTime(dateTime)
            .method("GET")
            .path("/test.txt")
            .headers(headers)
            .bodySha256(bodySha256)
            .params(params)
            .region("us-east-1")
            .ak("AKIAIOSFODNN7EXAMPLE")
            .sk("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")
            .bucket("examplebucket")
            .build()

        val request = sing.canonicalRequest()
        assertEquals(
            """
				GET
				/test.txt

				host:examplebucket.s3.amazonaws.com
				range:bytes=0-9
				x-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
				x-amz-date:20130524T000000Z

				host;range;x-amz-content-sha256;x-amz-date
				e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
				""".trimIndent(), request
        )

        val scopeDate = sing.date()
        val scope = sing.scope(scopeDate)

        val date = sing.headers.first(AwsUtils.HEADER_DATE)!!
        val source = sing.source(date, scope, request)
        assertEquals(
            """
				AWS4-HMAC-SHA256
				20130524T000000Z
				20130524/us-east-1/s3/aws4_request
				7344ae5b7ee6c3e7e6b0fe0640412a37625d1fbfff95c48bbb2dc43964946972
				""".trimIndent(), source
        )
        val sourceHmacSha = sing.sourceHmacSha(source, scopeDate)
        assertEquals("f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41", sourceHmacSha)
    }
}
