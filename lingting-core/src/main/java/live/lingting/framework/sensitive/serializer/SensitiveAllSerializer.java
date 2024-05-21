package live.lingting.framework.sensitive.serializer;

import live.lingting.framework.sensitive.Sensitive;
import live.lingting.framework.sensitive.SensitiveSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 全脱敏
 * <p>
 * 这是一个要脱敏的文本
 * </p>
 * <p>
 * *****
 * </p>
 * @author lingting 2024-05-21 10:20
 */
@SuppressWarnings("java:S6548")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensitiveAllSerializer implements SensitiveSerializer {

	public static final SensitiveAllSerializer INSTANCE = new SensitiveAllSerializer();

	@Override
	public String serialize(Sensitive sensitive, String raw) throws IOException {
		return sensitive.middle();
	}

}
