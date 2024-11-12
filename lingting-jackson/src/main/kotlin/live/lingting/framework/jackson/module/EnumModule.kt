package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import live.lingting.framework.util.EnumUtils
import java.io.IOException

/**
 * @author lingting 2022/12/20 14:11
 */
class EnumModule : SimpleModule() {
    init {
        init()
    }

    protected fun init() {
        addSerializer(Enum::class.java, EnumSerializer())

        val deserializers = EnumJacksonDeserializers()
        setDeserializers(deserializers)
    }

    class EnumSerializer : JsonSerializer<Enum<*>?>() {
        @Throws(IOException::class)
        override fun serialize(e: Enum<*>?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
            jsonGenerator.writeObject(EnumUtils.getValue(e))
        }
    }

    /**
     * @author lingting
     */
    class EnumJacksonDeserializers : SimpleDeserializers() {
        @Throws(JsonMappingException::class)
        override fun findEnumDeserializer(
            type: Class<*>, config: DeserializationConfig,
            beanDesc: BeanDescription
        ): JsonDeserializer<*> {
            if (type.isEnum) {
                return EnumDeserializer(type)
            }
            return super.findEnumDeserializer(type, config, beanDesc)
        }
    }

    class EnumDeserializer(private val cls: Class<*>) : JsonDeserializer<Enum<*>?>() {
        @Throws(IOException::class)
        override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Enum<*>? {
            // 获取前端输入的原始文本
            val rawString = jsonParser.valueAsString

            // 获取值
            for (obj in cls.enumConstants) {
                val e = obj as Enum<*>
                val value = EnumUtils.getValue(e)

                if (value == rawString
                    || (value != null && value.toString() == rawString)
                ) {
                    return e
                }
            }

            return null
        }
    }
}
