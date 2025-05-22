package live.lingting.framework.aws

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.value.multi.StringMultiValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-19 20:37
 */
class AwsV4SignerTest {

    @Test
    fun test() {
        val params = StringMultiValue()

        val headers = HttpHeaders.empty()
        headers.put("Host", "examplebucket.s3.amazonaws.com")
        headers.put("Range", "bytes=0-9")

        val signer = AwsV4Signer(
            HttpMethod.GET,
            "/test.txt",
            headers,
            null,
            params,
            "us-east-1",
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            "s3"
        )

        val bodyPayload = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        val dateTime = AwsUtils.parse("20130524T000000Z", signer.dateFormatter)
        val signed = signer.signed(dateTime, bodyPayload)

        assertEquals(bodyPayload, signed.bodyPayload)
        assertEquals("/test.txt", signed.canonicalUri)
        assertEquals("", signed.canonicalQuery)
        assertEquals(
            "host:examplebucket.s3.amazonaws.com\n" +
                    "range:bytes=0-9\n" +
                    "x-amz-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n" +
                    "x-amz-date:20130524T000000Z\n", signed.canonicalHeaders
        )
        assertEquals("host;range;x-amz-content-sha256;x-amz-date", signed.signedHeaders)
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
				""".trimIndent(), signed.canonicalRequest
        )
        assertEquals("20130524T000000Z", signed.date)
        assertEquals("20130524", signed.scopeDate)
        assertEquals("20130524/us-east-1/s3/aws4_request", signed.scope)
        assertEquals(
            """
				AWS4-HMAC-SHA256
				20130524T000000Z
				20130524/us-east-1/s3/aws4_request
				7344ae5b7ee6c3e7e6b0fe0640412a37625d1fbfff95c48bbb2dc43964946972
				""".trimIndent(), signed.source
        )

        assertEquals("f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41", signed.sign)
        assertEquals(
            "AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20130524/us-east-1/s3/aws4_request,SignedHeaders=host;range;x-amz-content-sha256;x-amz-date,Signature=f0e8bdb87c964420e857bd35b5d6ed310bd44f0170aba48dd91039c6036bdb41",
            signed.authorization
        )
    }
}
