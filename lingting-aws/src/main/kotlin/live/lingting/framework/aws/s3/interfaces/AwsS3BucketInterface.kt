package live.lingting.framework.aws.s3.interfaces

import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem
import java.util.function.Consumer

/**
 * @author lingting 2024-09-19 21:57
 */
interface AwsS3BucketInterface {
    fun use(key: String?): AwsS3ObjectInterface

    /**
     * 列举所有未完成的分片上传
     * @return k: uploadId, v: k
     */
    fun multipartList(): List<AwsS3MultipartItem?>?

    fun multipartList(consumer: Consumer<AwsS3SimpleRequest?>?): List<AwsS3MultipartItem?>?
}
