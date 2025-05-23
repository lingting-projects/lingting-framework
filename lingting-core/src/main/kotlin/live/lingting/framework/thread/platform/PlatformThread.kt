package live.lingting.framework.thread.platform

import live.lingting.framework.thread.executor.DelegationExecutorService
import live.lingting.framework.thread.executor.PolicyExecutorService
import live.lingting.framework.thread.executor.StateKeepExecutorService
import live.lingting.framework.thread.platform.PlatformThread.newExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor

/**
 * @author lingting 2025/5/12 10:20
 */
object PlatformThread : DelegationExecutorService(
    PolicyExecutorService(
        StateKeepExecutorService(
            newExecutor(
                Thread.ofPlatform()
                    .name("t-", 0)
                    .factory()
            )
        )
    )
) {

    @JvmStatic
    fun newExecutor(factory: ThreadFactory): ThreadPoolExecutor {
        val properties = ThreadPoolProperties.byDefault(factory)
        return properties.build()
    }

}
