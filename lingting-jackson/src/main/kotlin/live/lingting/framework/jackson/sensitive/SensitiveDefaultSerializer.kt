package live.lingting.framework.jackson.sensitive

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import live.lingting.framework.sensitive.Sensitive
import live.lingting.framework.sensitive.SensitiveSerializer
import live.lingting.framework.sensitive.SensitiveUtils

/**
 * @author lingting 2023-04-27 15:30
 */
class SensitiveDefaultSerializer(protected val sensitive: Sensitive) : JsonSerializer<Any?>(), SensitiveSerializer, ContextualSerializer {

    override fun createContextual(prov: SerializerProvider, property: BeanProperty): JsonSerializer<*> {
        val annotation = property.getAnnotation(Sensitive::class.java) ?: return prov.findValueSerializer(property.type, property)

        return SensitiveDefaultSerializer(annotation)
    }

    override fun serialize(raw: Any?, gen: JsonGenerator, serializers: SerializerProvider) {
        var raw = raw
        if (raw == null) {
            raw = ""
        }
        val `val` = serialize(sensitive, raw as String)
        gen.writeString(`val`)
    }

    override fun serialize(sensitive: Sensitive, raw: String): String {
        val serializer = SensitiveUtils.findSerializer(sensitive) ?: throw InvalidFormatException(null, "", raw, String::class.java)
        return serializer.serialize(sensitive, raw)
    }
}
