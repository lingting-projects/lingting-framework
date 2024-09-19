package live.lingting.framework.ali.sts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-14 13:51
 */
@Getter
@Setter
public abstract class AliStsResponse {

	@JsonProperty("RequestId")
	private String requestId;

}
