package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import java.math.BigDecimal
import live.lingting.framework.util.BooleanUtils


/**
 * @author lingting 2023-04-18 15:22
 */
@Suppress("UNCHECKED_CAST")
class BooleanModule : SimpleModule() {

    init {
        val deser = BooleanDeserializer()
        // kt包装了布尔导致Java类型不匹配
        addDeserializer(Class.forName("java.lang.Boolean") as Class<Boolean>, deser)
        addDeserializer(Boolean::class.java, deser)
    }

    class BooleanDeserializer : JsonDeserializer<Boolean?>() {

        override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Boolean? {
            return when (jsonParser.currentToken) {
                JsonToken.NOT_AVAILABLE, JsonToken.VALUE_NULL -> null
                JsonToken.VALUE_STRING -> {
                    val text = jsonParser.text.trim().lowercase()
                    if (BooleanUtils.isTrue(text)) {
                        return true
                    }
                    if (BooleanUtils.isFalse(text)) {
                        return false
                    }

                    // 转数值
                    try {
                        val decimal = BigDecimal(text)
                        return byNumber(decimal)
                    } catch (e: Exception) {
                        throw JsonParseException(
                            jsonParser,
                            "Converting text [$text] to Boolean is not supported!", e
                        )
                    }
                }

                JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT -> {
                    val decimal = jsonParser.decimalValue
                    return byNumber(decimal)
                }

                JsonToken.VALUE_TRUE -> true
                JsonToken.VALUE_FALSE -> false
                else -> throw JsonParseException(
                    jsonParser,
                    "Unable to convert type [${jsonParser.currentToken}] to boolean!"
                )
            }
        }

        fun byNumber(decimal: BigDecimal?): Boolean? {
            if (decimal == null) {
                return null
            }

            val compare = decimal.compareTo(BigDecimal.ZERO)
            return compare > 0
        }
    }

}
