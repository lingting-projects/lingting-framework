package live.lingting.framework.aws.s3;

import live.lingting.framework.aws.policy.Acl;
import live.lingting.framework.aws.policy.Credential;

/**
 * @author lingting 2024-09-12 21:20
 */
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

	public String getScheme() {return this.scheme;}

	public String getPrefix() {return this.prefix;}

	public String getConnector() {return this.connector;}

	public String getRegion() {return this.region;}

	public String getEndpoint() {return this.endpoint;}

	public String getBucket() {return this.bucket;}

	public Acl getAcl() {return this.acl;}

	public String getAk() {return this.ak;}

	public String getSk() {return this.sk;}

	public String getToken() {return this.token;}

	public void setScheme(String scheme) {this.scheme = scheme;}

	public void setPrefix(String prefix) {this.prefix = prefix;}

	public void setConnector(String connector) {this.connector = connector;}

	public void setRegion(String region) {this.region = region;}

	public void setEndpoint(String endpoint) {this.endpoint = endpoint;}

	public void setBucket(String bucket) {this.bucket = bucket;}

	public void setAcl(Acl acl) {this.acl = acl;}

	public void setAk(String ak) {this.ak = ak;}

	public void setSk(String sk) {this.sk = sk;}

	public void setToken(String token) {this.token = token;}
}
