package live.lingting.framework.ali.properties;

import live.lingting.framework.aws.s3.AwsS3Properties;

/**
 * @author lingting 2024-09-14 14:16
 */
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

	public AwsS3Properties s3() {
		AwsS3Properties s3 = new AwsS3Properties();
		s3.setScheme(scheme);
		s3.setConnector("-");
		s3.setRegion(region);
		s3.setEndpoint(endpoint);
		s3.setAk(ak);
		s3.setSk(sk);
		s3.setToken(token);
		return s3;
	}

	public String getScheme() {return this.scheme;}

	public String getPrefix() {return this.prefix;}

	public String getRegion() {return this.region;}

	public String getEndpoint() {return this.endpoint;}

	public String getAk() {return this.ak;}

	public String getSk() {return this.sk;}

	public String getToken() {return this.token;}

	public void setScheme(String scheme) {this.scheme = scheme;}

	public void setPrefix(String prefix) {this.prefix = prefix;}

	public void setRegion(String region) {this.region = region;}

	public void setEndpoint(String endpoint) {this.endpoint = endpoint;}

	public void setAk(String ak) {this.ak = ak;}

	public void setSk(String sk) {this.sk = sk;}

	public void setToken(String token) {this.token = token;}
}
