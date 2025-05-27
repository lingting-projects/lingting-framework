package live.lingting.framework.ali

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.value.multi.StringMultiValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2025/5/27 20:49
 */
class AliV3SignerTest {

    @Test
    fun test() {

        val headers = HttpHeaders.empty()
        headers.put("x-acs-signature-nonce", "3156853299f313e23d1673dc12e1703d")
        headers.put("x-acs-action", "RunInstances")
        headers.put("x-acs-version", "2014-05-26")
        headers.put("host", "ecs.cn-shanghai.aliyuncs.com")

        val time = AliUtils.parse("2023-10-26T10:22:32Z")

        val params = StringMultiValue()
        params.add("ImageId", "win2019_1809_x64_dtc_zh-cn_40G_alibase_20230811.vhd")
        params.add("RegionId", "cn-shanghai")

        val signer = AliV3Signer(
            HttpMethod.POST,
            "/",
            headers,
            null,
            params,
            "YourAccessKeyId",
            "YourAccessKeySecret"
        )

        val signed = signer.signed(time, "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
        assertEquals(
            "POST\n" +
                    "/\n" +
                    "ImageId=win2019_1809_x64_dtc_zh-cn_40G_alibase_20230811.vhd&RegionId=cn-shanghai\n" +
                    "host:ecs.cn-shanghai.aliyuncs.com\n" +
                    "x-acs-action:RunInstances\n" +
                    "x-acs-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n" +
                    "x-acs-date:2023-10-26T10:22:32Z\n" +
                    "x-acs-signature-nonce:3156853299f313e23d1673dc12e1703d\n" +
                    "x-acs-version:2014-05-26\n" +
                    "\n" +
                    "host;x-acs-action;x-acs-content-sha256;x-acs-date;x-acs-signature-nonce;x-acs-version\n" +
                    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", signed.canonicalRequest
        )

        assertEquals(
            "ACS3-HMAC-SHA256\n" +
                    "7ea06492da5221eba5297e897ce16e55f964061054b7695beedaac1145b1e259", signed.source
        )

        assertEquals("06563a9e1b43f5dfe96b81484da74bceab24a1d853912eee15083a6f0f3283c0", signed.sign)

        assertEquals(
            "ACS3-HMAC-SHA256 Credential=YourAccessKeyId,SignedHeaders=host;x-acs-action;x-acs-content-sha256;x-acs-date;x-acs-signature-nonce;x-acs-version,Signature=06563a9e1b43f5dfe96b81484da74bceab24a1d853912eee15083a6f0f3283c0",
            signed.authorization
        )

    }

}
