package live.lingting.framework.jackson.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * @author lingting
 */
class NullMapJsonSerializer : JsonSerializer<Any?>() {

    override fun serialize(value: Any?, jsonGenerator: JsonGenerator, provider: SerializerProvider) {
        if (value == null) {
            jsonGenerator.writeStartObject()
            jsonGenerator.writeEndObject()
        } else {
            jsonGenerator.writeObject(value)
        }
    }
}
