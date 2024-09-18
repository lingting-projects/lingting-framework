package live.lingting.framework.ali.properties;

import live.lingting.framework.s3.S3Properties;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-14 14:16
 */
@Getter
@Setter
public class AliProperties {

	protected String scheme = "https";

	protected String prefix;

	protected String region;

	protected String endpoint = "aliyuncs.com";

	protected String ak;

	protected String sk;

	protected String token;

	public String host() {
		return "%s://%s.%s.%s".formatted(scheme, prefix, region, endpoint);
	}

	public S3Properties s3() {
		S3Properties s3 = new S3Properties();
		s3.setScheme(scheme);
		s3.setRegion(region);
		s3.setEndpoint(endpoint);
		s3.setAk(ak);
		s3.setSk(sk);
		s3.setToken(token);
		return s3;
	}

}
