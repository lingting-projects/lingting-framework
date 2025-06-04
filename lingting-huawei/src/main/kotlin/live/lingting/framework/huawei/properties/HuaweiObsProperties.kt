package live.lingting.framework.huawei.properties

import live.lingting.framework.aws.properties.S3Properties

/**
 * @author lingting 2024-09-12 21:25
 */
class HuaweiObsProperties : S3Properties() {

    init {
        endpoint = "myhuaweicloud.com"
    }

    override fun copy(): HuaweiObsProperties {
        return HuaweiObsProperties().also { it.from(this) }
    }

    override fun secondHost(): String {
        return "obs.$region.$endpoint"
    }

}
