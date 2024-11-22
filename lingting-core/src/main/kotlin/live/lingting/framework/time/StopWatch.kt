package live.lingting.framework.time

import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * 参考 org.springframework.util.StopWatch
 *
 *
 * 线程不安全
 *
 *
 * @author lingting 2023-02-15 16:47
 */
class StopWatch {
    private var startTimeNanos: Long? = null

    /**
     * 耗时, 单位: 纳秒
     */
    private var durationNanos: Long? = null

    /**
     * 开始计时, 如果已开始, 则从延续之前的计时
     */
    fun start() {
        if (startTimeNanos != null) {
            return
        }
        durationNanos = null
        startTimeNanos = System.nanoTime()
    }

    val isRunning: Boolean
        /**
         * 是否正在运行
         */
        get() = startTimeNanos != null

    fun stop() {
        if (startTimeNanos != null) {
            durationNanos = System.nanoTime() - startTimeNanos!!
        }
        startTimeNanos = null
    }

    fun restart() {
        stop()
        start()
    }

    /**
     * 获取执行时长
     *
     *
     * 如果已开始, 则是开始时间到当前时长
     *
     *
     *
     * 如果已结束, 则是上一次统计时长
     *
     */
    fun timeNanos(): Long {
        return durationNanos ?: if (startTimeNanos == null) 0 else System.nanoTime() - startTimeNanos!!
    }

    fun timeMillis(): Long {
        val duration = timeNanos()
        return TimeUnit.NANOSECONDS.toMillis(duration)
    }

    fun duration(): Duration {
        val nanos = timeNanos()
        return Duration.ofNanos(nanos)
    }

}
