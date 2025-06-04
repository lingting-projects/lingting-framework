package live.lingting.framework.ali.properties

import live.lingting.framework.aws.properties.S3Properties

/**
 * @author lingting 2024-09-18 10:29
 */
class AliOssProperties : S3Properties() {

    init {
        endpoint = AliProperties.ENDPOINT
    }

    override fun copy(): AliOssProperties {
        return AliOssProperties().also { it.from(this) }
    }

    override fun secondHost(): String {
        return "oss-$region.$endpoint"
    }

}
