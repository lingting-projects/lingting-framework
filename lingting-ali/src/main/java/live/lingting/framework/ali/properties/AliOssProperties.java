package live.lingting.framework.ali.properties;

import live.lingting.framework.s3.Acl;
import live.lingting.framework.s3.Credential;
import live.lingting.framework.s3.S3Properties;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-18 10:29
 */
@Getter
@Setter
public class AliOssProperties extends AliProperties {

	private String bucket;

	private Acl acl = Acl.PRIVATE;

	public AliOssProperties() {
		setPrefix("oss");
	}

	@Override
	public String host() {
		return "%s://%s.%s-%s.%s".formatted(scheme, bucket, prefix, region, endpoint);
	}

	@Override
	public S3Properties s3() {
		S3Properties s3 = super.s3();
		s3.setBucket(bucket);
		s3.setAcl(acl);
		return s3;
	}

	public AliOssProperties copy() {
		AliOssProperties properties = new AliOssProperties();
		properties.setScheme(getScheme());
		properties.setPrefix(getPrefix());
		properties.setRegion(getRegion());
		properties.setEndpoint(getEndpoint());
		properties.setBucket(getBucket());
		properties.setAcl(getAcl());

		properties.setAk(getAk());
		properties.setSk(getSk());
		properties.setToken(getToken());
		return properties;
	}

	public void useCredential(Credential credential) {
		setAk(credential.getAk());
		setSk(credential.getSk());
		setToken(credential.getToken());
	}

}
