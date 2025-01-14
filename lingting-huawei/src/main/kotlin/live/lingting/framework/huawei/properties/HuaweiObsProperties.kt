package live.lingting.framework.huawei.properties

import live.lingting.framework.aws.s3.AwsS3Properties

/**
 * @author lingting 2024-09-12 21:25
 */
class HuaweiObsProperties : AwsS3Properties() {

    init {
        endpoint = "myhuaweicloud.com"
    }

    override fun copy(): HuaweiObsProperties {
        return HuaweiObsProperties().also { fill(it) }
    }

    override fun host(): String {
        return "$scheme://$bucket.obs.$region.$endpoint"
    }

}
