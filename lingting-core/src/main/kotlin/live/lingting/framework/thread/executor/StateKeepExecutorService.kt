package live.lingting.framework.thread.executor

import live.lingting.framework.function.StateKeepRunnable
import java.util.concurrent.ExecutorService

/**
 * @author lingting 2025/5/10 17:43
 */
open class StateKeepExecutorService(instance: ExecutorService) : DelegationExecutorService(instance) {

    fun execute(name: String?, command: Runnable) {
        val runnable = command as? StateKeepRunnable
            ?: object : StateKeepRunnable(name) {
                override fun doProcess() {
                    command.run()
                }
            }
        super.execute(runnable)
    }

    override fun execute(command: Runnable) {
        execute(null, command)
    }

}
