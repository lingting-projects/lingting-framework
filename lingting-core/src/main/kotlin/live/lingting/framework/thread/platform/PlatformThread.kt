package live.lingting.framework.thread.platform

import live.lingting.framework.thread.executor.PolicyThreadPoolExecutor
import live.lingting.framework.thread.executor.PolicyThreadPoolProperties
import live.lingting.framework.thread.executor.StateKeepExecutorService
import live.lingting.framework.thread.platform.PlatformThread.newExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor

/**
 * @author lingting 2025/5/12 10:20
 */
object PlatformThread : StateKeepExecutorService(newExecutor()) {

    @JvmStatic
    fun newExecutor(): ThreadPoolExecutor {
        return newExecutor(AtomicPlatformThreadFactory("t-"))
    }

    @JvmStatic
    fun newExecutor(factory: ThreadFactory): ThreadPoolExecutor {
        val properties = PolicyThreadPoolProperties.create(factory)
        return PolicyThreadPoolExecutor(properties)
    }

}
