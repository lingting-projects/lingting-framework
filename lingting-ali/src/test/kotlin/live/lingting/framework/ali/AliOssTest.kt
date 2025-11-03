package live.lingting.framework.ali

import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.S3BasicTest
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.util.ProxySelectorUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import java.net.InetSocketAddress
import java.time.Duration

/**
 * @author lingting 2024-09-18 14:29
 */
@EnabledIfSystemProperty(named = "framework.ali.oss.test", matches = "true")
class AliOssTest : S3BasicTest() {

    var sts: AliSts? = null

    var useSts = true

    var useProxy: Boolean = false

    @BeforeEach
    fun before() {
        if (useProxy) {
            val selector = ProxySelectorUtils.create(InetSocketAddress("127.0.0.1", 8888))
            client = HttpClient.okhttp()
                .disableSsl()
                .callTimeout(Duration.ofSeconds(10))
                .connectTimeout(Duration.ofSeconds(15))
                .readTimeout(Duration.ofSeconds(30))
                .proxySelector(selector)
                .build()
            ApiClient.defaultClient = client
        }

        sts = AliBasic.sts()
    }

    @Test
    fun test() {
        run()
    }

    override fun buildObj(key: String): AliOssObject =
        if (useSts) sts!!.ossObject(properties as AliOssProperties, key) else AliOssObject(
            AliBasic.ossProperties(),
            key
        )

    override fun buildBucket(): AliOssBucket =
        if (useSts) sts!!.ossBucket(properties as AliOssProperties) else AliOssBucket(AliBasic.ossProperties())

    override fun properties(): S3Properties = if (useSts) AliBasic.ossStsProperties() else AliBasic.ossProperties()

    override fun pre() {
        // 仅在非全球加速时测试预签名
        if (properties.region != AliUtils.REGION_ACCELERATE) {
            super.pre()
        }
    }

}
