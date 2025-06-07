package live.lingting.framework.ali.properties

import live.lingting.framework.ali.AliUtils

/**
 * @author lingting 2024-09-14 11:53
 */
class AliStsProperties : AliProperties() {

    var roleArn: String = ""

    var roleSessionName: String = ""

    override fun buildHost(): String {
        return buildString {
            append("sts.")
            if (region.isNotBlank() && AliUtils.REGION_ACCELERATE != region) {
                append(region).append(".")
            }
            append(endpoint)
        }
    }

}
