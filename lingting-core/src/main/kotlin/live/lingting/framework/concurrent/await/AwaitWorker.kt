package live.lingting.framework.concurrent.await

import java.time.Duration

/**
 * @author lingting 2025/5/13 13:54
 */
fun interface AwaitWorker<R> {

    /**
     * @param duration 已运行时长
     */
    fun get(duration: Duration): R?

}
