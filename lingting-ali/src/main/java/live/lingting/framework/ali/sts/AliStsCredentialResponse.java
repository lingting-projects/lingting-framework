package live.lingting.framework.ali.sts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lingting 2024-09-14 13:50
 */
public class AliStsCredentialResponse extends AliStsResponse {

	@JsonProperty("Credentials")
	private Credentials credentials;

	public String getAccessKeyId() {
		return getCredentials().getAccessKeyId();
	}

	public String getAccessKeySecret() {
		return getCredentials().getAccessKeySecret();
	}

	public String getSecurityToken() {
		return getCredentials().getSecurityToken();
	}

	public String getExpire() {
		return getCredentials().getExpiration();
	}

	public Credentials getCredentials() {return this.credentials;}

	@JsonProperty("Credentials")
	public void setCredentials(Credentials credentials) {this.credentials = credentials;}

	public static class Credentials {

		@JsonProperty("AccessKeyId")
		private String accessKeyId;

		@JsonProperty("AccessKeySecret")
		private String accessKeySecret;

		@JsonProperty("SecurityToken")
		private String securityToken;

		@JsonProperty("Expiration")
		private String expiration;

		public String getAccessKeyId() {return this.accessKeyId;}

		public String getAccessKeySecret() {return this.accessKeySecret;}

		public String getSecurityToken() {return this.securityToken;}

		public String getExpiration() {return this.expiration;}

		@JsonProperty("AccessKeyId")
		public void setAccessKeyId(String accessKeyId) {this.accessKeyId = accessKeyId;}

		@JsonProperty("AccessKeySecret")
		public void setAccessKeySecret(String accessKeySecret) {this.accessKeySecret = accessKeySecret;}

		@JsonProperty("SecurityToken")
		public void setSecurityToken(String securityToken) {this.securityToken = securityToken;}

		@JsonProperty("Expiration")
		public void setExpiration(String expiration) {this.expiration = expiration;}
	}

}
