package live.lingting.framework.jackson.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import live.lingting.framework.util.EnumUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Objects;

/**
 * @author lingting 2022/12/20 14:11
 */
@SuppressWarnings({ "java:S3740", "unchecked" })
public class EnumModule extends SimpleModule {

	public EnumModule() {
		init();
	}

	protected void init() {
		addSerializer(Enum.class, new EnumSerializer());

		EnumJacksonDeserializers deserializers = new EnumJacksonDeserializers();
		setDeserializers(deserializers);
	}

	public static class EnumSerializer extends JsonSerializer<Enum> {

		@Override
		public void serialize(Enum e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
				throws IOException {
			jsonGenerator.writeObject(EnumUtils.getValue(e));
		}

	}

	/**
	 * @author lingting
	 */
	public static class EnumJacksonDeserializers extends SimpleDeserializers {

		@Override
		public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config,
				BeanDescription beanDesc) throws JsonMappingException {
			if (type.isEnum()) {
				return new EnumDeserializer(type);
			}
			return super.findEnumDeserializer(type, config, beanDesc);
		}

	}

	@RequiredArgsConstructor
	public static class EnumDeserializer extends JsonDeserializer<Enum<?>> {

		private final Class<?> cls;

		@Override
		public Enum<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException {
			// 获取前端输入的原始文本
			String rawString = jsonParser.getValueAsString();

			// 获取值
			for (Object obj : cls.getEnumConstants()) {
				Enum<?> e = (Enum<?>) obj;
				Object value = EnumUtils.getValue(e);

				if (Objects.equals(value, rawString)
						|| (value != null && Objects.equals(value.toString(), rawString))) {
					return e;
				}
			}

			return null;
		}

	}

}
