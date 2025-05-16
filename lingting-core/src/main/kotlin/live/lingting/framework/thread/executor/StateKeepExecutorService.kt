package live.lingting.framework.thread.executor

import live.lingting.framework.function.StateKeepRunnable
import java.util.concurrent.ExecutorService

/**
 * @author lingting 2025/5/10 17:43
 */
open class StateKeepExecutorService(instance: ExecutorService) : DelegationExecutorService(instance) {

    override fun execute(command: Runnable) {
        if (command is StateKeepRunnable) {
            super.execute(command)
            return
        }

        val runnable = object : StateKeepRunnable() {
            override fun doProcess() {
                command.run()
            }
        }
        super.execute(runnable)
    }

}
