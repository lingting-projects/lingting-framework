package live.lingting.framework.aws.s3.interfaces

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
interface AwsS3ObjectInterface {
    // region get
    val key: String?

    fun publicUrl(): String?

    fun head(): HttpHeaders

    // endregion
    // region put
    @Throws(IOException::class)
    fun put(file: File?)

    @Throws(IOException::class)
    fun put(file: File?, acl: Acl?)

    @Throws(IOException::class)
    fun put(`in`: InputStream)

    @Throws(IOException::class)
    fun put(`in`: InputStream, acl: Acl?)

    fun put(`in`: CloneInputStream)

    fun put(`in`: CloneInputStream, acl: Acl?)

    fun delete()

    // endregion
    // region multipart
    fun multipartInit(): String?

    fun multipartInit(acl: Acl?): String?

    @Throws(IOException::class)
    fun multipart(source: InputStream?): AwsS3MultipartTask?

    @Throws(IOException::class)
    fun multipart(source: InputStream?, parSize: Long, async: Async): AwsS3MultipartTask

    fun multipartUpload(uploadId: String?, part: Part?, `in`: InputStream?): String?

    fun multipartMerge(uploadId: String?, map: Map<Part, String?>?)

    fun multipartCancel(uploadId: String) // endregion
}
