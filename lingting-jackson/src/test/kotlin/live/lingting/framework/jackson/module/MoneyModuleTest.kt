package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.type.TypeReference
import java.math.BigDecimal
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.money.Money
import live.lingting.framework.money.MoneyConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/11/19 14:18
 */
class MoneyModuleTest {

    @Test
    fun test() {
        val money = Money.of("5.423")
        val default = MoneyConfig.DEFAULT
        assertEquals(BigDecimal("5.423").setScale(default.decimalLimit, default.decimalType), money.value)
        // language=JSON
        val json = """
            {"v1":"5.42","v2":"8889928318498528745293753"}
        """.trimIndent()

        val map = JacksonUtils.toObj(json, object : TypeReference<Map<String, Money>>() {})
        assertEquals(money, map["v1"])
        assertEquals(Money.of("8889928318498528745293753"), map["v2"])
        val toJson = JacksonUtils.toJson(map)
        assertEquals(json, toJson)
    }
}
