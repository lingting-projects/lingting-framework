package live.lingting.framework.huawei.obs

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.value.multi.StringMultiValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * @author lingting 2024/11/5 13:53
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
class HuaweiObsSignTest {

    @Test
    fun test() {
        val headers = HttpHeaders.empty()
        headers.contentType("application/xml")
        val params = StringMultiValue()
        params.add("uploads")
        val signer = HuaweiObsSigner(
            HttpMethod.GET,
            "/bucket",
            headers,
            null,
            params,
            "ak",
            "sk"
        )
        val dateTime = HuaweiUtils.parse("Tue, 5 Nov 2024 06:26:17 GMT")
        val signed = signer.signed(dateTime)

        assertEquals(
            """
				GET

				application/xml
				Tue, 5 Nov 2024 06:26:17 GMT
				/bucket/?uploads
				""".trimIndent(), signed.source
        )
        assertEquals("OBS ak:Y4gIe5i9N4/x9lEtkU9UX6q2Bzw=", signed.authorization)
    }
}
