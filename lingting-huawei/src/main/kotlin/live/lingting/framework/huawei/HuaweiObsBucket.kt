package live.lingting.framework.huawei

import live.lingting.framework.aws.AwsS3Bucket
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.framework.huawei.properties.HuaweiObsProperties

/**
 * @author lingting 2024-09-13 14:48
 */
class HuaweiObsBucket(protected val properties: HuaweiObsProperties) : HuaweiObs<AwsS3Bucket?>(AwsS3Bucket(properties)), AwsS3BucketDelegation {
    override fun use(key: String?): HuaweiObsObject {
        return HuaweiObsObject(properties, key)
    }
}
