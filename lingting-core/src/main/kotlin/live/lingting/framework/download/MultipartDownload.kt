package live.lingting.framework.download

import java.io.File
import java.io.InputStream
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.function.Supplier
import live.lingting.framework.exception.DownloadException
import live.lingting.framework.kt.logger
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.thread.Async
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.ValueUtils

/**
 * @author lingting 2024-09-06 16:39
 */
@Suppress("UNCHECKED_CAST")
abstract class MultipartDownload<D : MultipartDownload<D>> protected constructor(builder: DownloadBuilder<*>) : Download {
    protected val log = logger()

    val url: String = builder.url

    /**
     * 文件大小. 不知道就写null. 为null或小于1时会调用 [MultipartDownload.size] 方法获取
     */
    val size: Long? = builder.size

    /**
     * 是否存在多个分片.
     */
    val isMulti: Boolean = builder.isMulti

    val threadLimit: Long = builder.threadLimit.toLong()

    val partSize: Long = builder.partSize

    val maxRetryCount: Long = builder.maxRetryCount

    val timeout: Duration? = builder.timeout

    protected val executor: ExecutorService = builder.executor

    protected val id: String = ValueUtils.simpleUuid()

    var downloadStatus: DownloadStatus = DownloadStatus.WAIT
        protected set

    var ex: DownloadException? = null
        protected set


    override val file = FileUtils.createTemp(id, TEMP_DIR)
        get() {
            await()
            val te = ex
            if (te != null) {
                throw te
            }
            return field
        }

    override fun start(): Download {
        if (!isStart && !isFinished) {
            synchronized(log) {
                if (!isStart && !isFinished) {
                    downloadStatus = DownloadStatus.RUNNING
                    doStart()
                }
            }
        }
        return this as D
    }

    protected fun doStart() {
        val async = if (threadLimit == Async.UNLIMITED) Async() else Async(threadLimit + 1)
        async.submit("download-$id") {
            try {
                val fileSize = if (size == null || size < 1) size() else size
                val multipart: Multipart = Multipart.builder()
                    .id(id)
                    .size(fileSize) // 不使用多分配下载时, 只设置一个分片
                    .partSize(if (isMulti) partSize else fileSize)
                    .build()
                val task = DownloadFileMultipartTask(
                    multipart, maxRetryCount, async, file
                ) { part -> download(part) }
                task.start().await(timeout)
                if (task.hasFailed()) {
                    val t = task.tasksFailed()[0]
                    throw t.t!!
                }
            } catch (e: Throwable) {
                ex = DownloadException("download start error!", e)
            } finally {
                downloadStatus = DownloadStatus.COMPLETED
            }
        }
    }

    override fun await(): Download {
        check(isStart) { "download not start!" }

        ValueUtils.awaitTrue(Supplier<Boolean> { this.isFinished })
        return this as D
    }

    override val isStart: Boolean
        get() = downloadStatus != DownloadStatus.WAIT

    override val isFinished: Boolean
        get() = downloadStatus == DownloadStatus.COMPLETED

    override val isSuccess: Boolean
        get() = isFinished && ex == null


    abstract fun size(): Long


    abstract fun download(part: Part): InputStream

    companion object {
        val TEMP_DIR: File = FileUtils.createTempDir("download")
    }
}
