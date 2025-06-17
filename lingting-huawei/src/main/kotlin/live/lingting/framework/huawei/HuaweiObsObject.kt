package live.lingting.framework.huawei

import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import live.lingting.framework.huawei.obs.HuaweiObsMeta
import live.lingting.framework.huawei.properties.HuaweiObsProperties

/**
 * @author lingting 2024-09-13 14:48
 */
class HuaweiObsObject(properties: HuaweiObsProperties, key: String) : HuaweiObs<AwsS3Object>(AwsS3Object(properties, key)), AwsS3ObjectDelegation {

    override fun head(): HuaweiObsMeta {
        val head = super<AwsS3ObjectDelegation>.head()
        return HuaweiObsMeta(head)
    }

}
