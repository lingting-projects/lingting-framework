package live.lingting.framework.aws.s3.interfaces

import java.io.InputStream
import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.multipart.Part
import live.lingting.framework.thread.Async

/**
 * @author lingting 2024-09-19 21:59
 */
interface AwsS3ObjectDelegation : AwsS3ObjectInterface, AwsS3Delegation<AwsS3Object> {
    override val key: String
        get() = delegation().key

    override fun publicUrl(): String {
        return delegation().publicUrl()
    }

    override fun head(): AwsS3Meta {
        return delegation().head()
    }

    override fun put(request: AwsS3ObjectPutRequest) {
        delegation().put(request)
    }

    override fun delete() {
        delegation().delete()
    }
    override fun multipartInit(acl: Acl?, meta: HttpHeaders?): String {
        return delegation().multipartInit(acl, meta)
    }

    override fun multipart(source: InputStream, parSize: Long, async: Async): AwsS3MultipartTask {
        return delegation().multipart(source, parSize, async)
    }

    override fun multipartUpload(uploadId: String, part: Part, input: InputStream): String {
        return delegation().multipartUpload(uploadId, part, input)
    }

    override fun multipartMerge(uploadId: String, map: Map<Part, String>) {
        delegation().multipartMerge(uploadId, map)
    }

    override fun multipartCancel(uploadId: String) {
        delegation().multipartCancel(uploadId)
    }
}
