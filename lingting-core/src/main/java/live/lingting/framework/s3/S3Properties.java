package live.lingting.framework.s3;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-12 21:20
 */
@Getter
@Setter
public class S3Properties {

	protected String scheme = "https";

	protected String region;

	protected String endpoint;

	protected String bucket;

	protected Acl acl = Acl.PRIVATE;

	protected String ak;

	protected String sk;

	protected String token;

	public <T extends S3Properties> T fill(T properties) {
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

	public S3Properties copy() {
		S3Properties properties = new S3Properties();
		fill(properties);
		return properties;
	}

	public void useCredential(Credential credential) {
		setAk(credential.getAk());
		setSk(credential.getSk());
		setToken(credential.getToken());
	}

}
