package live.lingting.framework.jackson.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import live.lingting.framework.money.Money;
import live.lingting.framework.util.StringUtils;

import java.io.IOException;

/**
 * @author lingting 2024-04-28 10:43
 */
public class MoneyModule extends SimpleModule {

	public MoneyModule() {
		addSerializer(Money.class, new MoneySerializer());
		addDeserializer(Money.class, new MoneyDeserializer());
	}

	public static class MoneySerializer extends JsonSerializer<Money> {

		@Override
		public void serialize(Money value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			// 使用原始字符串
			String jsonValue = value.toRawString();
			gen.writeString(jsonValue);
		}

	}

	public static class MoneyDeserializer extends JsonDeserializer<Money> {

		@Override
		public Money deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			String text = p.getText();
			if (!StringUtils.hasText(text)) {
				return null;
			}
			return Money.of(text);
		}

	}

}
