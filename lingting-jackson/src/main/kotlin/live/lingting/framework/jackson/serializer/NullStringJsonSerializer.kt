package live.lingting.framework.jackson.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * @author lingting
 */
class NullStringJsonSerializer : JsonSerializer<Any?>() {

    override fun serialize(value: Any?, jsonGenerator: JsonGenerator, provider: SerializerProvider) {
        jsonGenerator.writeString("")
    }
}
