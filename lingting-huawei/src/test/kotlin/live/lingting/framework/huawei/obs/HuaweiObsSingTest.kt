package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.s3.AwsS3Utils.PAYLOAD_UNSIGNED
import live.lingting.framework.http.header.HttpHeaders.Companion.empty
import live.lingting.framework.huawei.HuaweiUtils.parse
import live.lingting.framework.huawei.obs.HuaweiObsSing.Companion.builder
import live.lingting.framework.value.multi.StringMultiValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * @author lingting 2024/11/5 13:53
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
internal class HuaweiObsSingTest {
    @Test
    fun test() {
        val eHeaders = empty()
        eHeaders.add("x-obs-content-sha256", PAYLOAD_UNSIGNED)
        val eParams = StringMultiValue()
        eParams.add("uploads")
        val eDate = "Tue, 5 Nov 2024 06:26:17 GMT"
        val builder = builder()
            .ak("ak")
            .sk("sk")
            .method("get")
            .dateTime(parse(eDate))
            .bucket("bucket")
            .headers(eHeaders)
            .params(eParams)

        val sing = builder.build()

        assertEquals(
            """
				GET


				Tue, 5 Nov 2024 06:26:17 GMT
				x-obs-content-sha256:UNSIGNED-PAYLOAD
				/bucket/?uploads
				""".trimIndent(), sing.source()
        )
        assertEquals("OBS ak:DWeWvzUw0RaQpSPJCOTQKxQyBB8=", sing.calculate())
    }
}
