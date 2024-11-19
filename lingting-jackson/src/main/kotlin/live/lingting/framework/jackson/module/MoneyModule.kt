package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import live.lingting.framework.money.Money
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024-04-28 10:43
 */
class MoneyModule : SimpleModule() {
    init {
        addSerializer(Money::class.java, MoneySerializer())
        addDeserializer(Money::class.java, MoneyDeserializer())
    }

    class MoneySerializer : JsonSerializer<Money>() {

        override fun serialize(value: Money, gen: JsonGenerator, serializers: SerializerProvider) {
            // 使用原始字符串
            val jsonValue = value.toRawString()
            gen.writeString(jsonValue)
        }
    }

    class MoneyDeserializer : JsonDeserializer<Money?>() {

        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Money? {
            val text = p.text
            if (!StringUtils.hasText(text)) {
                return null
            }
            return Money.of(text)
        }
    }
}
