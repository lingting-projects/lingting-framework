package live.lingting.framework.sensitive;

import java.io.IOException;

/**
 * @author lingting 2024-01-26 17:56
 */
public interface SensitiveSerializer {

	/**
	 * 依据注解脱敏原始值
	 */
	String serialize(Sensitive sensitive, String raw) throws IOException;

}
