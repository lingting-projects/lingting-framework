package live.lingting.framework.ali.sts

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lingting 2024-09-14 13:51
 */
abstract class AliStsResponse {
    @JsonProperty("RequestId")
    var requestId: String = ""
}
