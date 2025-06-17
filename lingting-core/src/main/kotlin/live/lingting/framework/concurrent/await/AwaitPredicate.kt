package live.lingting.framework.concurrent.await

import java.time.Duration

/**
 * @author lingting 2025/5/13 14:12
 */
fun interface AwaitPredicate<R> {

    /**
     * @return true 表示等待到了目标值
     */
    fun test(duration: Duration, r: R?): Boolean

}
