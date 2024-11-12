package live.lingting.framework.jackson.sensitive;

import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveProvider;
import live.lingting.framework.sensitive.SensitiveSerializer;

import java.io.IOException;

/**
 * @author lingting 2024-01-29 10:39
 */
public class SensitiveSpiProvider implements SensitiveProvider {

	@Override
	public SensitiveSerializer find(Sensitive sensitive) {
		if (SensitiveSpiSerializer.class.isAssignableFrom(sensitive.value())) {
			return new SensitiveSpiSerializer();
		}
		return null;
	}

	public static class SensitiveSpiSerializer implements SensitiveSerializer {

		@Override
		public String serialize(Sensitive sensitive, String raw) throws IOException {
			return "*";
		}

	}

}
