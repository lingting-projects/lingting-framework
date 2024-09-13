package live.lingting.framework.huawei.iam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-13 11:52
 */
@Getter
@Setter
public class HuaweiIamTokenResponse {

	private Token token;

	public String getExpire() {
		return getToken().getExpire();
	}

	public String getIssued() {
		return getToken().getIssued();
	}

	@Getter
	@Setter
	public static class Token {

		@JsonProperty("expires_at")
		private String expire;

		@JsonProperty("issued_at")
		private String issued;

	}

}
