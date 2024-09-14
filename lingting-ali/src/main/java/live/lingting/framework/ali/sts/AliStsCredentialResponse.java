package live.lingting.framework.ali.sts;

import com.fasterxml.jackson.annotation.JsonProperty;
import live.lingting.framework.ali.AliResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-14 13:50
 */
@Getter
@Setter
public class AliStsCredentialResponse extends AliResponse {

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

	@Getter
	@Setter
	public static class Credentials {

		@JsonProperty("AccessKeyId")
		private String accessKeyId;

		@JsonProperty("AccessKeySecret")
		private String accessKeySecret;

		@JsonProperty("SecurityToken")
		private String securityToken;

		@JsonProperty("Expiration")
		private String expiration;

	}

}
