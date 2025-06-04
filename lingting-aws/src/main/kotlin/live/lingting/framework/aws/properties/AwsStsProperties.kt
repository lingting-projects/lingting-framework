package live.lingting.framework.aws.properties

/**
 * @author lingting 2025/6/3 15:45
 */
open class AwsStsProperties : AwsProperties() {

    var roleArn: String = ""

    var roleSessionName: String = ""

    var sourceIdentity = ""

    override fun host(): String {
        return "sts.$endpoint"
    }

}
