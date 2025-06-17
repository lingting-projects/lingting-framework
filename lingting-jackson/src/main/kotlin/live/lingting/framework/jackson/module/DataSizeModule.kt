package live.lingting.framework.jackson.module

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import live.lingting.framework.data.DataSize

/**
 * @author lingting 2025/4/9 16:15
 */
class DataSizeModule : SimpleModule() {

    init {
        addSerializer(DataSize::class.java, DataSizeSerializer())
        addDeserializer(DataSize::class.java, DataSizeDeserializer())
    }

    class DataSizeSerializer : JsonSerializer<DataSize>() {

        override fun serialize(value: DataSize, gen: JsonGenerator, serializers: SerializerProvider) {
            val bytes = value.bytes
            gen.writeNumber(bytes)
        }
    }

    class DataSizeDeserializer : JsonDeserializer<DataSize?>() {

        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DataSize? {
            val bytes = p.text.toLongOrNull()
            if (bytes == null || bytes < 0) {
                return null
            }
            return DataSize.ofBytes(bytes)
        }
    }

}
