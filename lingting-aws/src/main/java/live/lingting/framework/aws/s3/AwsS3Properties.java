package live.lingting.framework.aws.s3;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.aws.policy.Credential;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-12 21:20
 */
@Getter
@Setter
public class AwsS3Properties {

	protected String scheme = "https";

	protected String prefix = "s3";

	protected String connector = ".";

	protected String region;

	protected String endpoint = "amazonaws.com";

	protected String bucket;

	protected Acl acl = Acl.PRIVATE;

	protected String ak;

	protected String sk;

	protected String token;

	public <T extends AwsS3Properties> T fill(T properties) {
		properties.setScheme(getScheme());
		properties.setRegion(getRegion());
		properties.setEndpoint(getEndpoint());
		properties.setBucket(getBucket());
		properties.setAcl(getAcl());
		properties.setAk(getAk());
		properties.setSk(getSk());
		properties.setToken(getToken());
		return properties;
	}

	public AwsS3Properties copy() {
		AwsS3Properties properties = new AwsS3Properties();
		fill(properties);
		return properties;
	}

	public void useCredential(Credential credential) {
		setAk(credential.getAk());
		setSk(credential.getSk());
		setToken(credential.getToken());
	}

	public String host() {
		return "%s://%s.%s%s%s.%s".formatted(scheme, bucket, prefix, connector, region, endpoint);
	}

}
