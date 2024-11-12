package live.lingting.framework.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * @author lingting 2023-07-24 18:23
 */
public class InstantSerializer extends JsonSerializer<Instant> {

	private final DateTimeFormatter formatter;

	public InstantSerializer() {
		this.formatter = DateTimeFormatter.ISO_INSTANT;
	}

	public InstantSerializer(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		String format = formatter.format(value);
		gen.writeString(format);
	}

}
