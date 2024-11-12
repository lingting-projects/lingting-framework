package live.lingting.framework.jackson.module;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import live.lingting.framework.util.BooleanUtils;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author lingting 2023-04-18 15:22
 */
public class BooleanModule extends SimpleModule {

	public BooleanModule() {
		init();
	}

	protected void init() {
		super.addDeserializer(Boolean.class, new BooleanDeserializer());
	}

	public static class BooleanDeserializer extends JsonDeserializer<Boolean> {

		@Override
		public Boolean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException {
			return switch (jsonParser.getCurrentToken()) {
				case NOT_AVAILABLE, VALUE_NULL -> null;
				case VALUE_STRING -> {
					String text = jsonParser.getText().trim().toLowerCase();
					if (BooleanUtils.isTrue(text)) {
						yield true;
					}
					if (BooleanUtils.isFalse(text)) {
						yield false;
					}

					// 转数值
					try {
						BigDecimal decimal = new BigDecimal(text);
						yield byNumber(decimal);
					}
					catch (Exception e) {
						throw new JsonParseException(jsonParser,
								"Converting text [%s] to Boolean is not supported!".formatted(text), e);
					}
				}
				case VALUE_NUMBER_INT, VALUE_NUMBER_FLOAT -> {
					BigDecimal decimal = jsonParser.getDecimalValue();
					yield byNumber(decimal);
				}
				case VALUE_TRUE -> true;
				case VALUE_FALSE -> false;
				default -> throw new JsonParseException(jsonParser,
						"Unable to convert type [%s] to boolean!".formatted(jsonParser.getCurrentToken()));
			};

		}

		Boolean byNumber(BigDecimal decimal) {
			if (decimal == null) {
				return null;
			}

			int compare = decimal.compareTo(BigDecimal.ZERO);
			return compare > 0;
		}

	}

}
