package live.lingting.framework.aws.s3

import live.lingting.framework.aws.AwsS3Object
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.multipart.file.FileMultipartTask
import live.lingting.framework.thread.Async
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024-09-19 20:26
 */
open class AwsS3MultipartTask(multipart: Multipart, async: Async, protected val s3: AwsS3Object) : FileMultipartTask<AwsS3MultipartTask>(multipart, async) {
    protected val map: MutableMap<Part, String> = ConcurrentHashMap(multipart.parts.size)

    @JvmField
    val uploadId: String = multipart.id

    constructor(multipart: Multipart, s3: AwsS3Object) : this(multipart, Async(), s3)

    init {
        maxRetryCount = 10
    }

    override fun onMerge() {
        try {
            s3.multipartMerge(uploadId, map)
        } catch (e: Exception) {
            log.warn("[AwsS3MultipartTask] [{}] onMerge exception", uploadId, e)
            onCancel()
            log.debug("[AwsS3MultipartTask] [{}] onCanceled", uploadId)
        }
    }

    override fun onCancel() {
        s3.multipartCancel(uploadId)
    }

    override fun onPart(part: Part) {
        multipart.stream(part).use {
            val etag = s3.multipartUpload(uploadId, part, it)
            map.put(part, etag)
        }
    }
}
