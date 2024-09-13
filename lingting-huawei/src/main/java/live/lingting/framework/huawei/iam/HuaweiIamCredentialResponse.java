package live.lingting.framework.huawei.iam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-13 14:00
 */
@Getter
@Setter
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

	@Getter
	@Setter
	public static class Credential {

		private String access;

		private String secret;

		@JsonProperty("securitytoken")
		private String securityToken;

		@JsonProperty("expires_at")
		private String expire;

	}

}
