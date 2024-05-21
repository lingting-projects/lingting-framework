package live.lingting.framework.sensitive.serializer;

import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveSerializer;
import live.lingting.framework.sensitive.SensitiveUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 手机号格式脱敏
 * <p>
 * +8617612349876
 * </p>
 * <p>
 * +86*****76
 * </p>
 * @author lingting 2024-05-21 10:20
 */
@SuppressWarnings("java:S6548")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensitiveMobileSerializer implements SensitiveSerializer {

	public static final SensitiveMobileSerializer INSTANCE = new SensitiveMobileSerializer();

	@Override
	public String serialize(Sensitive sensitive, String raw) throws IOException {
		if (raw.startsWith("+")) {
			String serialize = SensitiveUtils.serialize(raw.substring(1), 2, 2, sensitive);
			return "+" + serialize;
		}
		return SensitiveUtils.serialize(raw, 2, 2, sensitive);
	}

}
