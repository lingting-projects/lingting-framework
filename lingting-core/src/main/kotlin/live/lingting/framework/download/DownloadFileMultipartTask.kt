package live.lingting.framework.download

import live.lingting.framework.function.ThrowableFunction
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.multipart.PartTask
import live.lingting.framework.multipart.file.FileMultipartTask
import live.lingting.framework.stream.RandomAccessOutputStream
import live.lingting.framework.thread.Async
import live.lingting.framework.util.StreamUtils
import java.io.File
import java.io.InputStream

/**
 * @author lingting 2024-09-06 16:55
 */
open class DownloadFileMultipartTask(
    multipart: Multipart,
    maxRetryCount: Long,
    async: Async,
    target: File,
    download: ThrowableFunction<Part, InputStream>
) : FileMultipartTask<DownloadFileMultipartTask>(multipart, async) {

    protected val target: File

    protected val download: ThrowableFunction<Part, InputStream>

    protected constructor(
        multipart: Multipart, maxRetryCount: Long,
        download: ThrowableFunction<Part, InputStream>, target: File
    ) : this(multipart, maxRetryCount, Async(), target, download)

    init {
        this.maxRetryCount = maxRetryCount
        this.target = target
        this.download = download
    }

    override fun onMerge() {
        //
    }

    override fun onCancel() {
        //
    }

    override fun onPart(part: Part) {
        RandomAccessOutputStream(target).use { output ->
            output.seek(part.start.bytes)
            download.apply(part).use { input ->
                StreamUtils.write(input, output)
            }
        }
    }

    override fun allowRetry(task: PartTask, t: Throwable?): Boolean {
        return task.retryCount < maxRetryCount
    }

}
