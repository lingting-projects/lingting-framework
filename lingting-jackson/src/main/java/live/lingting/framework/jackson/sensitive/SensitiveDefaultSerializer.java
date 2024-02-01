package live.lingting.framework.jackson.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import live.lingting.framework.Sequence;
import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveProvider;
import live.lingting.framework.sensitive.SensitiveSerializer;
import live.lingting.framework.sensitive.SensitiveUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

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
		return switch (sensitive.value()) {
			case ALL -> sensitive.middle();
			case DEFAULT -> SensitiveUtils.serialize(raw, 1, 1, sensitive);
			case MOBILE -> {
				if (raw.startsWith("+")) {
					String serialize = SensitiveUtils.serialize(raw.substring(1), 2, 2, sensitive);
					yield "+" + serialize;
				}
				yield SensitiveUtils.serialize(raw, 2, 2, sensitive);
			}
			case CUSTOMER -> {
				SensitiveSerializer serializer = findSerializer(sensitive);
				if (serializer == null) {
					throw new InvalidFormatException(null, "", raw, String.class);
				}
				yield serializer.serialize(sensitive, raw);
			}
			default -> raw;
		};
	}

	protected SensitiveSerializer findSerializer(Sensitive sensitive) {
		List<SensitiveProvider> providers = providers();
		for (SensitiveProvider provider : providers) {
			SensitiveSerializer serializer = provider.find(sensitive);
			if (sensitive != null) {
				return serializer;
			}
		}
		return null;
	}

	protected List<SensitiveProvider> providers() {
		try {
			ServiceLoader<SensitiveProvider> loader = ServiceLoader.load(SensitiveProvider.class);
			return loader.stream()
				.map(ServiceLoader.Provider::get)
				.filter(Objects::nonNull)
				.sorted(Sequence.INSTANCE_ASC)
				.toList();
		}
		catch (ServiceConfigurationError e) {
			return Collections.emptyList();
		}
	}

}
