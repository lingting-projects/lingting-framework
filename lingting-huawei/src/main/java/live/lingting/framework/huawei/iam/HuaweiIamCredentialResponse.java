package live.lingting.framework.huawei.iam;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lingting 2024-09-13 14:00
 */
public class HuaweiIamCredentialResponse {

	private Credential credential;

	public String getAccess() {
		return getCredential().getAccess();
	}

	public String getSecret() {
		return getCredential().getSecret();
	}

	public String getSecurityToken() {
		return getCredential().getSecurityToken();
	}

	public String getExpire() {
		return getCredential().getExpire();
	}

	public Credential getCredential() {return this.credential;}

	public void setCredential(Credential credential) {this.credential = credential;}

	public static class Credential {

		private String access;

		private String secret;

		@JsonProperty("securitytoken")
		private String securityToken;

		@JsonProperty("expires_at")
		private String expire;

		public String getAccess() {return this.access;}

		public String getSecret() {return this.secret;}

		public String getSecurityToken() {return this.securityToken;}

		public String getExpire() {return this.expire;}

		public void setAccess(String access) {this.access = access;}

		public void setSecret(String secret) {this.secret = secret;}

		@JsonProperty("securitytoken")
		public void setSecurityToken(String securityToken) {this.securityToken = securityToken;}

		@JsonProperty("expires_at")
		public void setExpire(String expire) {this.expire = expire;}
	}

}
