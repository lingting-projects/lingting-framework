package live.lingting.framework.jackson

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.time.LocalDateTime
import live.lingting.framework.jackson.module.BooleanModule
import live.lingting.framework.jackson.module.EnumModule
import live.lingting.framework.jackson.module.JavaTimeMillisModule
import live.lingting.framework.jackson.module.JavaTimeModule
import live.lingting.framework.jackson.module.MoneyModule
import live.lingting.framework.jackson.module.RModule
import live.lingting.framework.jackson.provider.NullSerializerProvider
import live.lingting.framework.jackson.sensitive.SensitiveModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2025/1/9 16:20
 */
class ObjectMapperTest {

    val modules = listOf(
        EnumModule(),
        BooleanModule(),
        RModule(),
        SensitiveModule(),
        MoneyModule(),
    )

    @Test
    fun testShare() {
        val m1 = ObjectMapper().also { share(it) }.apply {
            registerModule(JavaTimeModule())
            // provider 不能共享
            setSerializerProvider(NullSerializerProvider())
        }
        val m2 = ObjectMapper().also { share(it) }.apply {
            registerModule(JavaTimeMillisModule())
            setSerializerProvider(NullSerializerProvider())
        }

        val time = LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0)
        assertEquals("\"2020-01-01 00:00:00\"", m1.writeValueAsString(time))
        assertEquals("\"2020-01-01 00:00:00.000\"", m2.writeValueAsString(time))

        val bean = Bean().also { it.time = time }
        val beanData = BeanData().also { it.time = time }
        assertEquals("{\"time\":\"2020-01-01 00:00:00\"}", m1.writeValueAsString(bean))
        assertEquals("{\"time\":\"2020-01-01 00:00:00\"}", m1.writeValueAsString(beanData))
        assertEquals("{\"time\":\"2020-01-01 00:00:00.000\"}", m2.writeValueAsString(bean))
        assertEquals("{\"time\":\"2020-01-01 00:00:00.000\"}", m2.writeValueAsString(beanData))
    }


    fun share(mapper: ObjectMapper) {
        // 序列化时忽略未知属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        // 单值元素可以被设置成 array, 防止处理 ["a"] 为 List<String> 时报错
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        // 空对象不报错
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        // 有特殊需要转义字符, 不报错
        mapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature())
        modules.forEach {
            mapper.registerModule(it)
        }
    }

}

class Bean {
    var time: LocalDateTime? = null
}

data class BeanData(
    var time: LocalDateTime? = null
) {
}
