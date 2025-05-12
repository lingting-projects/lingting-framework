package live.lingting.framework.util

import live.lingting.framework.function.StateKeepRunnable
import live.lingting.framework.thread.executor.DelegationExecutorService
import live.lingting.framework.thread.virtual.VirtualThread

/**
 * @author lingting 2023-11-15 16:44
 */
object ThreadUtils : DelegationExecutorService(VirtualThread) {

    fun execute(name: String?, command: Runnable) {
        val runnable = object : StateKeepRunnable(name) {
            override fun doProcess() {
                command.run()
            }
        }
        execute(runnable)
    }

}

