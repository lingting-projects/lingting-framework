package live.lingting.framework.download

import live.lingting.framework.data.DataSize
import live.lingting.framework.util.ThreadUtils
import java.time.Duration
import java.util.concurrent.ExecutorService

/**
 * @author lingting 2024-01-16 19:33
 */
@Suppress("UNCHECKED_CAST")
abstract class DownloadBuilder<B : DownloadBuilder<B>> protected constructor(
    /**
     * 文件下载地址
     */
    val url: String
) {
    var isMulti: Boolean = false

    var executor: ExecutorService = ThreadUtils.executor()

    /**
     * 文件大小, 用于多线程下载时进行分片. 单位: bytes
     * 设置为null或者小于1时调用size方法解析
     */
    var size: DataSize? = null

    /**
     * 最大启动线程数
     */
    var threadLimit: Int = DEFAULT_THREAD_LIMIT

    /**
     * 每个分片的最大大小, 单位: bytes
     */
    var partSize: DataSize = DEFAULT_PART_SIZE

    var maxRetryCount: Long = DEFAULT_MAX_RETRY_COUNT

    var timeout: Duration? = null

    fun executor(executor: ExecutorService): B {
        this.executor = executor
        return this as B
    }

    fun single(): B {
        this.isMulti = false
        return this as B
    }

    fun multi(): B {
        this.isMulti = true
        return this as B
    }

    fun size(size: DataSize): B {
        this.size = size
        return this as B
    }

    fun threadLimit(maxThreadCount: Int): B {
        this.threadLimit = safeDefault(maxThreadCount, DEFAULT_THREAD_LIMIT)
        return this as B
    }

    fun partSize(partSize: DataSize): B {
        this.partSize = safeDefault(partSize, DEFAULT_PART_SIZE)
        return this as B
    }

    fun maxRetryCount(maxRetryCount: Long): B {
        this.maxRetryCount = safeDefault(maxRetryCount, DEFAULT_MAX_RETRY_COUNT)
        return this as B
    }

    fun timeout(timeout: Duration): B {
        this.timeout = safeDefault(timeout, DEFAULT_TIMEOUT)
        return this as B
    }

    abstract fun build(): Download

    /**
     * 将原值进行安全判断, 如果不满足则设置为默认值
     * @param t 原值
     * @param d 默认值
     * @return 结果
     */
    protected fun <T> safeDefault(t: T, d: T): T {
        if (t == null) {
            return d
        }
        if (t is Number && t.toLong() < 1) {
            return d
        }
        if (t is Duration && t.isNegative) {
            return d
        }
        return t
    }

    companion object {
        protected const val DEFAULT_THREAD_LIMIT: Int = 20

        protected val DEFAULT_PART_SIZE = DataSize.ofMb(10)

        protected const val DEFAULT_MAX_RETRY_COUNT: Long = 3

        @JvmStatic
        protected val DEFAULT_TIMEOUT: Duration? = null
    }
}
