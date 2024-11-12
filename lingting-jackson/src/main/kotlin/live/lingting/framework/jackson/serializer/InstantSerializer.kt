package live.lingting.framework.jackson.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2023-07-24 18:23
 */
class InstantSerializer : JsonSerializer<Instant> {
    private val formatter: DateTimeFormatter

    constructor() {
        this.formatter = DateTimeFormatter.ISO_INSTANT
    }

    constructor(formatter: DateTimeFormatter) {
        this.formatter = formatter
    }

    @Throws(IOException::class)
    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider) {
        val format = formatter.format(value)
        gen.writeString(format)
    }
}
