package live.lingting.framework.sensitive.serializer;

import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveSerializer;
import live.lingting.framework.sensitive.SensitiveUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 默认脱敏
 * <p>
 * 这是一个要脱敏的文本
 * </p>
 * <p>
 * 这*****本
 * </p>
 * @author lingting 2024-05-21 10:20
 */
@SuppressWarnings("java:S6548")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensitiveDefaultSerializer implements SensitiveSerializer {

	public static final SensitiveDefaultSerializer INSTANCE = new SensitiveDefaultSerializer();

	@Override
	public String serialize(Sensitive sensitive, String raw) throws IOException {
		return SensitiveUtils.serialize(raw, 1, 1, sensitive);
	}

}
