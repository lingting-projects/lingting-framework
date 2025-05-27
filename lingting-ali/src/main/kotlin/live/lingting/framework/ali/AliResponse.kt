package live.lingting.framework.ali

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lingting 2024-09-14 13:51
 */
abstract class AliResponse {

    @JsonProperty("RequestId")
    var requestId: String = ""

}
