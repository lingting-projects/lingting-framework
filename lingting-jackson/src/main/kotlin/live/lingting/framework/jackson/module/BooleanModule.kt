package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import java.io.IOException
import java.math.BigDecimal
import live.lingting.framework.util.BooleanUtils


/**
 * @author lingting 2023-04-18 15:22
 */
class BooleanModule : SimpleModule() {
    init {
        init()
    }

    protected fun init() {
        super.addDeserializer(Boolean::class.java, BooleanDeserializer())
    }

    class BooleanDeserializer : JsonDeserializer<Boolean?>() {
        @Throws(IOException::class)
        override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Boolean? {
            return when (jsonParser.currentToken) {
                JsonToken.NOT_AVAILABLE, JsonToken.VALUE_NULL -> null
                JsonToken.VALUE_STRING -> {
                    val text = jsonParser.text.trim().lowercase(Locale.getDefault())
                    if (BooleanUtils.isTrue(text)) {
                        true
                    }
                    if (BooleanUtils.isFalse(text)) {
                        false
                    }

                    // 转数值
                    try {
                        val decimal = BigDecimal(text)
                        byNumber(decimal)
                    } catch (e: Exception) {
                        throw JsonParseException(
                            jsonParser,
                            "Converting text [%s] to Boolean is not supported!".formatted(text), e
                        )
                    }
                }

                JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT -> {
                    val decimal = jsonParser.decimalValue
                    byNumber(decimal)
                }

                JsonToken.VALUE_TRUE -> true
                JsonToken.VALUE_FALSE -> false
                else -> throw JsonParseException(
                    jsonParser,
                    "Unable to convert type [%s] to boolean!".formatted(jsonParser.currentToken)
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
