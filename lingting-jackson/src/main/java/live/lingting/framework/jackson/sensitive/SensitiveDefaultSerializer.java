package live.lingting.framework.jackson.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveSerializer;
import live.lingting.framework.sensitive.SensitiveUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * @author lingting 2023-04-27 15:30
 */
@RequiredArgsConstructor
public class SensitiveDefaultSerializer extends JsonSerializer<Object>
		implements SensitiveSerializer, ContextualSerializer {

	protected final Sensitive sensitive;

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
			throws JsonMappingException {
		Sensitive annotation = property.getAnnotation(Sensitive.class);
		if (annotation == null) {
			return prov.findValueSerializer(property.getType(), property);
		}

		return new SensitiveDefaultSerializer(annotation);
	}

	@Override
	public void serialize(Object raw, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (raw == null) {
			raw = "";
		}
		String val = serialize(sensitive, (String) raw);
		gen.writeString(val);
	}

	@Override
	public String serialize(Sensitive sensitive, String raw) throws IOException {
		SensitiveSerializer serializer = SensitiveUtils.findSerializer(sensitive);
		if (serializer == null) {
			throw new InvalidFormatException(null, "", raw, String.class);
		}
		return serializer.serialize(sensitive, raw);
	}

}
