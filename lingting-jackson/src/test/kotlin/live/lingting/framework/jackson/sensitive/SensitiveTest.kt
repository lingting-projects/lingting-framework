package live.lingting.framework.jackson.sensitive

import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.jackson.sensitive.SensitiveSpiProvider.SensitiveSpiSerializer
import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.serializer.SensitiveAllSerializer
import live.lingting.framework.sensitive.serializer.SensitiveMobileSerializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-04-27 15:53
 */
internal class SensitiveTest {
    @Test
    fun sensitive() {
        val test = SensitiveTestEntity()
        test.all = "all"
        test.defaultValue = "default"
        test.mobile = "8677711113333"
        test.spi = "spi"
        val json = JacksonUtils.toJson(test)
        println(json)
        val node = JacksonUtils.toNode(json)
        Assertions.assertEquals("******", node["all"].asText())
        Assertions.assertEquals("d******t", node["defaultValue"].asText())
        Assertions.assertEquals("86******33", node["mobile"].asText())
        Assertions.assertEquals("*", node["spi"].asText())
    }

    internal class SensitiveTestEntity {
        @Sensitive(SensitiveAllSerializer::class)
        var all: String? = null

        @Sensitive
        var defaultValue: String? = null

        @Sensitive(SensitiveMobileSerializer::class)
        var mobile: String? = null

        @Sensitive(SensitiveSpiSerializer::class)
        var spi: String? = null
    }
}
