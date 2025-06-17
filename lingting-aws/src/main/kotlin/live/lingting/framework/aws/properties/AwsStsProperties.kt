package live.lingting.framework.aws.properties

/**
 * @author lingting 2025/6/3 15:45
 */
open class AwsStsProperties : AwsProperties() {

    init {
        region = ""
    }

    var roleArn: String = ""

    var roleSessionName: String = ""

    var sourceIdentity = ""

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
