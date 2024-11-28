package live.lingting.framework.aws.s3.interfaces

import java.io.File
import java.io.InputStream
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.multipart.Part
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.thread.Async

/**
 * @author lingting 2024-09-19 21:59
 */
interface AwsS3ObjectInterface {
    // region get
    val key: String

    fun publicUrl(): String

    fun head(): HttpHeaders

    // endregion

    // region put

    fun put(file: File)

    fun put(file: File, acl: Acl?)

    fun put(input: InputStream)

    fun put(input: InputStream, acl: Acl?)

    fun put(input: CloneInputStream)

    fun put(input: CloneInputStream, acl: Acl?)

    fun delete()

    // endregion

    // region multipart
    fun multipartInit(): String

    fun multipartInit(acl: Acl?): String

    fun multipart(source: InputStream): AwsS3MultipartTask

    fun multipart(source: InputStream, parSize: Long, async: Async): AwsS3MultipartTask

    fun multipartUpload(uploadId: String, part: Part, input: InputStream): String

    fun multipartMerge(uploadId: String, map: Map<Part, String>)

    fun multipartCancel(uploadId: String) // endregion
}
