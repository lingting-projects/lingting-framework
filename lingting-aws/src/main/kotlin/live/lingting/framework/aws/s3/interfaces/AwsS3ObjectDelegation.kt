package live.lingting.framework.aws.s3.interfaces

import java.io.File
import java.io.InputStream
import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.multipart.Part
import live.lingting.framework.stream.CloneInputStream
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

    override fun head(): HttpHeaders {
        return delegation().head()
    }

    override fun put(file: File) {
        delegation().put(file)
    }

    override fun put(file: File, acl: Acl?) {
        delegation().put(file, acl)
    }

    override fun put(input: InputStream) {
        delegation().put(input)
    }

    override fun put(input: InputStream, acl: Acl?) {
        delegation().put(input, acl)
    }

    override fun put(input: CloneInputStream) {
        delegation().put(input)
    }

    override fun put(input: CloneInputStream, acl: Acl?) {
        delegation().put(input, acl)
    }

    override fun delete() {
        delegation().delete()
    }

    override fun multipartInit(): String {
        return delegation().multipartInit()
    }

    override fun multipartInit(acl: Acl?): String {
        return delegation().multipartInit(acl)
    }

    override fun multipart(source: InputStream): AwsS3MultipartTask {
        return delegation().multipart(source)
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
