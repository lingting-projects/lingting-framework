package live.lingting.framework.ali.properties

/**
 * @author lingting 2024-09-14 11:53
 */
class AliStsProperties : AliProperties() {
    @JvmField
    var roleArn: String? = null

    @JvmField
    var roleSessionName: String? = null

    init {
        setPrefix("sts")
    }
}
