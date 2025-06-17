package live.lingting.framework.concurrent.await

import java.time.Duration

/**
 * @author lingting 2025/5/13 14:23
 */
fun interface AwaitOnTimeout<R> {

    fun on(timeout: Duration, r: AwaitRunnable<R>)

}
