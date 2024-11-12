package live.lingting.framework.jackson.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException

/**
 * @author lingting
 */
class NullArrayJsonSerializer : JsonSerializer<Any?>() {
    @Throws(IOException::class)
    override fun serialize(value: Any?, jsonGenerator: JsonGenerator, provider: SerializerProvider) {
        if (value == null) {
            jsonGenerator.writeStartArray()
            jsonGenerator.writeEndArray()
        } else {
            jsonGenerator.writeObject(value)
        }
    }
}
