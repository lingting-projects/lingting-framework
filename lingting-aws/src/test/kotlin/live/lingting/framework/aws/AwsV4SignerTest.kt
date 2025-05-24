package live.lingting.framework.aws

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.DurationUtils.days
import live.lingting.framework.value.multi.StringMultiValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-19 20:37
 */
class AwsV4SignerTest {

    @Test
    fun test() {
        testHeader()
        testParams()
        testParamsToken()
    }

    fun testParamsToken() {
        val params = StringMultiValue()

        val builder = HttpUrlBuilder().https()
            .host("examplebucket.s3.us-east-1.amazonaws.com")
            .path("test.txt")

        val headers = HttpHeaders.empty()
        headers.put("Host", builder.headerHost())
        headers.put(
            AwsUtils.HEADER_TOKEN,
            "IQoJb3JpZ2luX2VjEMv//////////wEaCXVzLWVhc3QtMSJGMEQCIBSUbVdj9YGs2g0HkHsOHFdkwOozjARSKHL987NhhOC8AiBPepRU1obMvIbGU0T+WphFPgK/qpxaf5Snvm5M57XFkCqlAgjz//////////8BEAAaDDQ3MjM4NTU0NDY2MCIM83pULBe5/+Nm1GZBKvkBVslSaJVgwSef7SsoZCJlfJ56weYl3QCwEGr2F4BmCZZyFpmWEYzWnhNK1AnHMj5nkfKlKBx30XAT5PZGVrmq4Vkn9ewlXQy1Iu3QJRi9Tdod8Ef9/yajTaUGh76+F5u5a4O115jwultOQiKomVwO318CO4l8lv/3HhMOkpdanMXn+4PY8lvM8RgnzSu90jOUpGXEOAo/6G8OqlMim3+ZmaQmasn4VYRvESEd7O72QGZ3+vDnDVnss0lSYjlv8PP7IujnvhZRnj0WoeOyMe1lL0wTG/a9usH5hE52w/YUJccOn0OaZuyROuVsRV4Q70sbWQhUvYUt+0tUMKzm8vsFOp4BaNZFqobbjtb36Y92v+x5kY6i0s8QE886jJtUWMP5ldMziClGx3p0mN5dzsYlM3GyiJ/O1mWkPQDwg3mtSpOA9oeeuAMPTA7qMqy9RNuTKBDSx9EW27wvPzBum3SJhEfxv48euadKgrIX3Z79ruQFSQOc9LUrDjR+4SoWAJqK+GX8Q3vPSjsLxhqhEMWd6U4TXcM7ku3gxMbzqfT8NDg="
        )

        val signer = AwsV4Signer(
            HttpMethod.GET,
            builder.buildPath(),
            headers,
            null,
            params,
            "us-east-1",
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            "s3"
        )

        val dateTime = AwsUtils.parse("20200524T000000Z", signer.dateFormatter)

        val signed = signer.signed(dateTime, 1.days)

        assertEquals(
            "GET\n" +
                    "/test.txt\n" +
                    "X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20200524%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20200524T000000Z&X-Amz-Expires=86400&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEMv%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJGMEQCIBSUbVdj9YGs2g0HkHsOHFdkwOozjARSKHL987NhhOC8AiBPepRU1obMvIbGU0T%2BWphFPgK%2Fqpxaf5Snvm5M57XFkCqlAgjz%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDQ3MjM4NTU0NDY2MCIM83pULBe5%2F%2BNm1GZBKvkBVslSaJVgwSef7SsoZCJlfJ56weYl3QCwEGr2F4BmCZZyFpmWEYzWnhNK1AnHMj5nkfKlKBx30XAT5PZGVrmq4Vkn9ewlXQy1Iu3QJRi9Tdod8Ef9%2FyajTaUGh76%2BF5u5a4O115jwultOQiKomVwO318CO4l8lv%2F3HhMOkpdanMXn%2B4PY8lvM8RgnzSu90jOUpGXEOAo%2F6G8OqlMim3%2BZmaQmasn4VYRvESEd7O72QGZ3%2BvDnDVnss0lSYjlv8PP7IujnvhZRnj0WoeOyMe1lL0wTG%2Fa9usH5hE52w%2FYUJccOn0OaZuyROuVsRV4Q70sbWQhUvYUt%2B0tUMKzm8vsFOp4BaNZFqobbjtb36Y92v%2Bx5kY6i0s8QE886jJtUWMP5ldMziClGx3p0mN5dzsYlM3GyiJ%2FO1mWkPQDwg3mtSpOA9oeeuAMPTA7qMqy9RNuTKBDSx9EW27wvPzBum3SJhEfxv48euadKgrIX3Z79ruQFSQOc9LUrDjR%2B4SoWAJqK%2BGX8Q3vPSjsLxhqhEMWd6U4TXcM7ku3gxMbzqfT8NDg%3D&X-Amz-SignedHeaders=host\n" +
                    "host:examplebucket.s3.us-east-1.amazonaws.com\n" +
                    "\n" +
                    "host\n" +
                    "UNSIGNED-PAYLOAD", signed.canonicalRequest
        )

        assertEquals(
            "AWS4-HMAC-SHA256\n" +
                    "20200524T000000Z\n" +
                    "20200524/us-east-1/s3/aws4_request\n" +
                    "e0fd1f46f1c003e6dd2b3a4948ddf8424d0b008401cf1be3d755a472bab41948", signed.source
        )

        assertEquals("c2d49056b303f7a68fd414fe3a3bb07c3bc76e18cdd3f88194e969638cbdfce1", signed.sign)

        signed.params?.run { builder.addParams(this) }

        assertEquals(
            "https://examplebucket.s3.us-east-1.amazonaws.com/test.txt" +
                    "?X-Amz-Algorithm=AWS4-HMAC-SHA256" +
                    "&X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20200524%2Fus-east-1%2Fs3%2Faws4_request" +
                    "&X-Amz-Date=20200524T000000Z&X-Amz-Expires=86400" +
                    "&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEMv%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJGMEQCIBSUbVdj9YGs2g0HkHsOHFdkwOozjARSKHL987NhhOC8AiBPepRU1obMvIbGU0T%2BWphFPgK%2Fqpxaf5Snvm5M57XFkCqlAgjz%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDQ3MjM4NTU0NDY2MCIM83pULBe5%2F%2BNm1GZBKvkBVslSaJVgwSef7SsoZCJlfJ56weYl3QCwEGr2F4BmCZZyFpmWEYzWnhNK1AnHMj5nkfKlKBx30XAT5PZGVrmq4Vkn9ewlXQy1Iu3QJRi9Tdod8Ef9%2FyajTaUGh76%2BF5u5a4O115jwultOQiKomVwO318CO4l8lv%2F3HhMOkpdanMXn%2B4PY8lvM8RgnzSu90jOUpGXEOAo%2F6G8OqlMim3%2BZmaQmasn4VYRvESEd7O72QGZ3%2BvDnDVnss0lSYjlv8PP7IujnvhZRnj0WoeOyMe1lL0wTG%2Fa9usH5hE52w%2FYUJccOn0OaZuyROuVsRV4Q70sbWQhUvYUt%2B0tUMKzm8vsFOp4BaNZFqobbjtb36Y92v%2Bx5kY6i0s8QE886jJtUWMP5ldMziClGx3p0mN5dzsYlM3GyiJ%2FO1mWkPQDwg3mtSpOA9oeeuAMPTA7qMqy9RNuTKBDSx9EW27wvPzBum3SJhEfxv48euadKgrIX3Z79ruQFSQOc9LUrDjR%2B4SoWAJqK%2BGX8Q3vPSjsLxhqhEMWd6U4TXcM7ku3gxMbzqfT8NDg%3D" +
                    "&X-Amz-Signature=c2d49056b303f7a68fd414fe3a3bb07c3bc76e18cdd3f88194e969638cbdfce1" +
                    "&X-Amz-SignedHeaders=host", builder.build()
        )
    }

    fun testParams() {
        val params = StringMultiValue()

        val builder = HttpUrlBuilder().https()
            .host("examplebucket.s3.amazonaws.com")
            .path("test.txt")

        val headers = HttpHeaders.empty()
        headers.put("Host", builder.headerHost())

        val signer = AwsV4Signer(
            HttpMethod.GET,
            builder.buildPath(),
            headers,
            null,
            params,
            "us-east-1",
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            "s3"
        )

        val dateTime = AwsUtils.parse("20130524T000000Z", signer.dateFormatter)

        val signed = signer.signed(dateTime, 1.days)

        assertEquals(
            "GET\n" +
                    "/test.txt\n" +
                    "X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20130524%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20130524T000000Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host\n" +
                    "host:examplebucket.s3.amazonaws.com\n" +
                    "\n" +
                    "host\n" +
                    "UNSIGNED-PAYLOAD", signed.canonicalRequest
        )

        assertEquals(
            "AWS4-HMAC-SHA256\n" +
                    "20130524T000000Z\n" +
                    "20130524/us-east-1/s3/aws4_request\n" +
                    "3bfa292879f6447bbcda7001decf97f4a54dc650c8942174ae0a9121cf58ad04", signed.source
        )

        assertEquals("aeeed9bbccd4d02ee5c0109b86d86835f995330da4c265957d157751f604d404", signed.sign)

        signed.params?.run { builder.addParams(this) }

        assertEquals(
            "https://examplebucket.s3.amazonaws.com/test.txt?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20130524%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20130524T000000Z&X-Amz-Expires=86400&X-Amz-Signature=aeeed9bbccd4d02ee5c0109b86d86835f995330da4c265957d157751f604d404&X-Amz-SignedHeaders=host",
            builder.build()
        )
    }

    fun testHeader() {
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
