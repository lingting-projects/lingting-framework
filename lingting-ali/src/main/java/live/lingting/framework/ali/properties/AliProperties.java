package live.lingting.framework.ali.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-14 14:16
 */
@Getter
@Setter
public class AliProperties {

	protected String scheme = "https";

	private String prefix;

	private String region;

	private String endpoint = "aliyuncs.com";

	private String ak;

	private String sk;

	private String token;

	public String host() {
		return "%s://%s.%s.%s".formatted(scheme, prefix, region, endpoint);
	}

}
