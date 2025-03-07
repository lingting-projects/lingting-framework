package live.lingting.framework.ali.properties

/**
 * @author lingting 2024-09-18 10:29
 */
class AliOssProperties : AliProperties() {

    override fun copy(): AliOssProperties {
        return AliOssProperties().also { it.from(this) }
    }

    override fun secondHost(): String {
        return "oss-$region.$endpoint"
    }

}
