package live.lingting.framework.ali.properties

/**
 * @author lingting 2024-09-18 10:29
 */
class AliOssProperties : AliProperties() {

    override fun host(): String {
        return "$scheme://$bucket.oss-$region.$endpoint"
    }

    override fun copy(): AliOssProperties {
        return AliOssProperties().also { fill(it) }
    }

}
