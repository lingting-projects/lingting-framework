package live.lingting.framework.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author lingting
 */
public class NullStringJsonSerializer extends JsonSerializer<Object> {

	@Override
	public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
		jsonGenerator.writeString("");
	}

}
