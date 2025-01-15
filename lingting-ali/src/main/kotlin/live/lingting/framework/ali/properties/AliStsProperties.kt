package live.lingting.framework.ali.properties

/**
 * @author lingting 2024-09-14 11:53
 */
class AliStsProperties : AliProperties() {

    var roleArn: String = ""

    var roleSessionName: String = ""

    override fun secondHost(): String {
        return "sts.$region.$endpoint"
    }

}
