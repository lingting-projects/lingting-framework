package live.lingting.framework.ali.properties

/**
 * @author lingting 2024-09-14 11:53
 */
class AliStsProperties : AliProperties() {

    var roleArn: String = ""

    var roleSessionName: String = ""

    override fun host(): String {
        return buildString {
            append("sts.")
            if (region.isNotBlank()) {
                append(region).append(".")
            }
            append(endpoint)
        }
    }

}
