package live.lingting.framework.thread.virtual

import live.lingting.framework.thread.executor.StateKeepExecutorService

/**
 * @author lingting 2025/5/12 10:10
 */
object VirtualThread : StateKeepExecutorService(VirtualThreadExecutorService()) {

    @JvmField
    val isSupport: Boolean = VirtualThreadExecutorService.isSupport

}
