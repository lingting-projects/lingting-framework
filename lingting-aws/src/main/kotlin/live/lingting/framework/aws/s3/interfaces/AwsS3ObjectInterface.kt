package live.lingting.framework.aws.s3.interfaces

import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.aws.s3.AwsS3MultipartTask
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.framework.data.DataSize
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.multipart.Part
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.thread.Async
import java.io.File
import java.io.InputStream

/**
 * @author lingting 2024-09-19 21:59
 */
interface AwsS3ObjectInterface {

    // region get
    val key: String

    fun publicUrl(): String

    fun head(): AwsS3Meta

    // endregion

    // region put

    fun put(file: File) = put(file, null as Acl?)

    fun put(file: File, acl: Acl?) = put(FileCloneInputStream(file), acl)

    fun put(file: File, meta: HttpHeaders?) = put(FileCloneInputStream(file), meta)

    fun put(input: InputStream) = put(input, null as Acl?)

    fun put(input: InputStream, acl: Acl?) = put(input, acl, null)

    fun put(input: InputStream, meta: HttpHeaders?) = put(input, null, meta)

    fun put(input: InputStream, acl: Acl?, meta: HttpHeaders?) = input.use {
        val request = AwsS3ObjectPutRequest().also {
            it.stream = input
            it.acl = acl
            meta?.run { it.meta.addAll(this) }
        }
        put(request)
    }

    fun put(input: CloneInputStream) = put(input, null as Acl?)

    fun put(input: CloneInputStream, acl: Acl?) = put(input, acl, null)

    fun put(input: CloneInputStream, meta: HttpHeaders?) = put(input, null, meta)

    fun put(input: CloneInputStream, acl: Acl?, meta: HttpHeaders?) = put(input as InputStream, acl, null)

    fun put(request: AwsS3ObjectPutRequest)

    fun delete()

    // endregion

    // region multipart
    fun multipartInit() = multipartInit(null)

    fun multipartInit(acl: Acl?) = multipartInit(acl, null)

    fun multipartInit(acl: Acl?, meta: HttpHeaders?): String

    fun multipart(source: InputStream) = multipart(source, Async(20))

    fun multipart(source: InputStream, async: Async) = multipart(source, AwsUtils.MULTIPART_DEFAULT_PART_SIZE, async)

    fun multipart(source: InputStream, parSize: DataSize, async: Async): AwsS3MultipartTask

    fun multipartUpload(uploadId: String, part: Part, input: InputStream): String

    fun multipartMerge(uploadId: String, map: Map<Part, String>)

    fun multipartCancel(uploadId: String)

    // endregion

}
