package live.lingting.framework.huawei

import live.lingting.framework.aws.AwsS3Bucket
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.framework.aws.s3.request.AwsS3ListObjectRequest
import live.lingting.framework.aws.s3.response.AwsS3ListObjectResponse
import live.lingting.framework.huawei.properties.HuaweiObsProperties

/**
 * @author lingting 2024-09-13 14:48
 */
open class HuaweiObsBucket(protected val properties: HuaweiObsProperties) : HuaweiObs<AwsS3Bucket>(AwsS3Bucket(properties)), AwsS3BucketDelegation {

    override fun use(key: String): HuaweiObsObject {
        return HuaweiObsObject(properties, key)
    }

    override fun listObjects(request: AwsS3ListObjectRequest): AwsS3ListObjectResponse {
        request.v2 = false
        return super.listObjects(request)
    }
}
