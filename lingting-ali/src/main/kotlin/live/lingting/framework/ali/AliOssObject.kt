package live.lingting.framework.ali

import live.lingting.framework.ali.oss.AliOssMeta
import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation

/**
 * @author lingting 2024-09-19 21:23
 */
class AliOssObject(properties: AliOssProperties, key: String) : AliOss<AwsS3Object>(AwsS3Object(properties, key)),
    AwsS3ObjectDelegation {

    override fun head(): AliOssMeta {
        val head = super<AwsS3ObjectDelegation>.head()
        return AliOssMeta(head)
    }

}
