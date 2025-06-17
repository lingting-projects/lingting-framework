package live.lingting.framework.aws.s3.interfaces

import java.util.function.Consumer
import live.lingting.framework.aws.s3.request.AwsS3ListObjectRequest
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3ListObjectResponse
import live.lingting.framework.aws.s3.response.AwsS3MultipartItem
import live.lingting.framework.value.CursorValue
import live.lingting.framework.value.cursor.FunctionCursorValue

/**
 * @author lingting 2024-09-19 21:57
 */
interface AwsS3BucketInterface {

    fun use(key: String): AwsS3ObjectInterface

    /**
     * 列举所有未完成的分片上传
     * @return k: uploadId, v: k
     */
    fun multipartList(): List<AwsS3MultipartItem>

    fun multipartList(consumer: Consumer<AwsS3SimpleRequest>?): List<AwsS3MultipartItem>

    fun listObjects() = listObjects(true)

    fun listObjects(v2: Boolean) = listObjects(AwsS3ListObjectRequest().also {
        it.v2 = v2
    })

    fun listObjects(prefix: String) = listObjects(AwsS3ListObjectRequest().also {
        it.prefix = prefix
    })

    fun listObjects(request: AwsS3ListObjectRequest): AwsS3ListObjectResponse

    fun cursorObjects(request: AwsS3ListObjectRequest): CursorValue<AwsS3ListObjectResponse.Content> {
        return FunctionCursorValue(request) {
            val response = listObjects(it)
            FunctionCursorValue.Item(response.nextRequest(), response.contents)
        }
    }

}
