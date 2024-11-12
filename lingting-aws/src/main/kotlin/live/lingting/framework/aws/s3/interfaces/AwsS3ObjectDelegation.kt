package live.lingting.framework.aws.s3.interfaces

import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.multipart.Part
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.thread.Async
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * @author lingting 2024-09-19 21:59
 */
interface AwsS3ObjectDelegation : AwsS3ObjectInterface, AwsS3Delegation<AwsS3Object?> {
    override val key: String?
        get() = delegation().getKey()

    override fun publicUrl(): String? {
        return delegation()!!.publicUrl()
    }

    override fun head(): HttpHeaders {
        return delegation()!!.head()
    }

    @Throws(IOException::class)
    override fun put(file: File?) {
        delegation()!!.put(file)
    }

    @Throws(IOException::class)
    override fun put(file: File?, acl: Acl?) {
        delegation()!!.put(file, acl)
    }

    @Throws(IOException::class)
    override fun put(`in`: InputStream) {
        delegation()!!.put(`in`)
    }

    @Throws(IOException::class)
    override fun put(`in`: InputStream, acl: Acl?) {
        delegation()!!.put(`in`, acl)
    }

    override fun put(`in`: CloneInputStream) {
        delegation()!!.put(`in`)
    }

    override fun put(`in`: CloneInputStream, acl: Acl?) {
        delegation()!!.put(`in`, acl)
    }

    override fun delete() {
        delegation()!!.delete()
    }

    override fun multipartInit(): String? {
        return delegation()!!.multipartInit()
    }

    override fun multipartInit(acl: Acl?): String? {
        return delegation()!!.multipartInit(acl)
    }

    @Throws(IOException::class)
    override fun multipart(source: InputStream?): AwsS3MultipartTask? {
        return delegation()!!.multipart(source)
    }

    @Throws(IOException::class)
    override fun multipart(source: InputStream?, parSize: Long, async: Async): AwsS3MultipartTask {
        return delegation()!!.multipart(source, parSize, async)
    }

    override fun multipartUpload(uploadId: String?, part: Part?, `in`: InputStream?): String? {
        return delegation()!!.multipartUpload(uploadId, part, `in`)
    }

    override fun multipartMerge(uploadId: String?, map: Map<Part, String?>?) {
        delegation()!!.multipartMerge(uploadId, map)
    }

    override fun multipartCancel(uploadId: String) {
        delegation()!!.multipartCancel(uploadId)
    }
}
