package live.lingting.framework.huawei.multipart

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.huawei.HuaweiObsObject
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.multipart.file.FileMultipartTask
import live.lingting.framework.retry.Retry
import live.lingting.framework.thread.Async

/**
 * @author lingting 2024-09-13 20:37
 */
class HuaweiMultipartTask(
    multipart: Multipart,
    async: Async,
    protected val obsObject: HuaweiObsObject
) : FileMultipartTask<HuaweiMultipartTask>(multipart, async) {
    protected val map: MutableMap<Part, String> = ConcurrentHashMap(multipart.parts.size)

    val uploadId: String = multipart.id

    constructor(multipart: Multipart, obsObject: HuaweiObsObject) : this(multipart, Async(), obsObject)

    init {
        maxRetryCount = 10
    }

    override fun onMerge() {
        log.debug("[{}] onMerge", uploadId)

        val retry: Retry<Void?> = Retry.simple(maxRetryCount.toInt(), Duration.ofSeconds(1)) {
            var ex: Exception? = null
            try {
                obsObject.multipartMerge(uploadId, map)
            } catch (e: Exception) {
                ex = e
            }

            // 尝试进行校验
            if (ex != null) {
                val headUploadId = obsObject.head().multipartUploadId()
                // 不匹配, 未合并成功. 抛出异常 稍后重试
                if (headUploadId != uploadId) {
                    log.debug("[{}] onMerge retry", uploadId)
                    throw ex
                }
            }
            null
        }

        try {
            retry.get()
        } catch (e: Exception) {
            log.warn("[{}] onMerge exception", uploadId, e)
            onCancel()
            log.debug("[{}] onCanceled", uploadId)
        }
    }

    override fun onCancel() {
        obsObject.multipartCancel(uploadId)
    }


    override fun onPart(part: Part) {
        multipart.stream(part).use { `in` ->
            val etag = obsObject.multipartUpload(uploadId, part, `in`)
            map.put(part, etag)
        }
    }
}
