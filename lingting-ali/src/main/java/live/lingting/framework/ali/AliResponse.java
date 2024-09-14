package live.lingting.framework.ali;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lingting 2024-09-14 13:51
 */
@Getter
@Setter
public abstract class AliResponse {

	@JsonProperty("RequestId")
	private String requestId;
}
