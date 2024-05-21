package live.lingting.framework.sensitive.serializer;

import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveProvider;
import live.lingting.framework.sensitive.SensitiveSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author lingting 2024-05-21 10:30
 */
@SuppressWarnings("java:S6548")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensitiveDefaultProvider implements SensitiveProvider {

	public static final SensitiveDefaultProvider INSTANCE = new SensitiveDefaultProvider();

	@Override
	public SensitiveSerializer find(Sensitive sensitive) {
		if (SensitiveAllSerializer.class.isAssignableFrom(sensitive.value())) {
			return SensitiveAllSerializer.INSTANCE;
		}

		if (SensitiveMobileSerializer.class.isAssignableFrom(sensitive.value())) {
			return SensitiveMobileSerializer.INSTANCE;
		}
		return SensitiveDefaultSerializer.INSTANCE;
	}

	@Override
	public int getSequence() {
		return Integer.MAX_VALUE;
	}

}
