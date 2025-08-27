package live.lingting.framework.aws.s3.interfaces

import live.lingting.framework.aws.AwsS3Bucket
import live.lingting.framework.aws.s3.request.AwsS3ListObjectRequest
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3ListObjectResponse
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem
import java.util.function.Consumer

/**
 * @author lingting 2024-09-19 21:55
 */
interface AwsS3BucketDelegation : AwsS3BucketInterface, AwsS3Delegation<AwsS3Bucket> {

    override fun use(key: String): AwsS3ObjectInterface {
        return delegation().use(key)
    }

    override fun multipartList(): List<AwsS3MultipartItem> {
        return delegation().multipartList()
    }

    override fun multipartList(consumer: Consumer<AwsS3SimpleRequest>?): List<AwsS3MultipartItem> {
        return delegation().multipartList(consumer)
    }

    override fun listObjects(request: AwsS3ListObjectRequest): AwsS3ListObjectResponse {
        return delegation().listObjects(request)
    }
}
