package live.lingting.framework.huawei.iam;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lingting 2024-09-13 11:52
 */
public class HuaweiIamTokenResponse {

	private Token token;

	public String getExpire() {
		return getToken().getExpire();
	}

	public String getIssued() {
		return getToken().getIssued();
	}

	public Token getToken() {return this.token;}

	public void setToken(Token token) {this.token = token;}

	public static class Token {

		@JsonProperty("expires_at")
		private String expire;

		@JsonProperty("issued_at")
		private String issued;

		public String getExpire() {return this.expire;}

		public String getIssued() {return this.issued;}

		@JsonProperty("expires_at")
		public void setExpire(String expire) {this.expire = expire;}

		@JsonProperty("issued_at")
		public void setIssued(String issued) {this.issued = issued;}
	}

}
