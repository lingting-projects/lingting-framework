package live.lingting.framework.aws.s3.interfaces

import java.util.function.Consumer
import live.lingting.framework.aws.AwsS3Bucket
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem

/**
 * @author lingting 2024-09-19 21:55
 */
interface AwsS3BucketDelegation : AwsS3BucketInterface, AwsS3Delegation<AwsS3Bucket> {
    override fun multipartList(): List<AwsS3MultipartItem> {
        return delegation().multipartList()
    }

    override fun multipartList(consumer: Consumer<AwsS3SimpleRequest>?): List<AwsS3MultipartItem> {
        return delegation().multipartList(consumer)
    }
}
