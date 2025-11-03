package live.lingting.framework.aws.s3.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lingting 2025/5/22 16:17
 */
class AwsS3PreSignedResponse(
    /**
     * 值必须为编码后的url
     */
    @JsonProperty("url")
    val url: String,
    @JsonProperty("headers")
    val headers: Map<String, List<String>>
) {

    override fun toString(): String {
        return "[${headers.size}] $url"
    }

}
