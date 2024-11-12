package live.lingting.framework.ali.sts;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lingting 2024-09-14 13:51
 */
public abstract class AliStsResponse {

	@JsonProperty("RequestId")
	private String requestId;

	public String getRequestId() {return this.requestId;}

	@JsonProperty("RequestId")
	public void setRequestId(String requestId) {this.requestId = requestId;}
}
