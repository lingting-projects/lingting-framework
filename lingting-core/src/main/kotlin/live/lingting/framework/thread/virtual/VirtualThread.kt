package live.lingting.framework.thread.virtual

import live.lingting.framework.thread.executor.DelegationExecutorService
import live.lingting.framework.thread.executor.PolicyExecutorService

/**
 * @author lingting 2025/5/12 10:10
 */
object VirtualThread : DelegationExecutorService(PolicyExecutorService(VirtualThreadExecutorService())) {

    @JvmField
    val isSupport: Boolean = VirtualThreadExecutorService.isSupport

}
