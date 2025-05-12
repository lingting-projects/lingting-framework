package live.lingting.framework.util

import live.lingting.framework.thread.executor.DelegationExecutorService
import live.lingting.framework.thread.virtual.VirtualThread

/**
 * @author lingting 2023-11-15 16:44
 */
object ThreadUtils : DelegationExecutorService(VirtualThread) {

}

