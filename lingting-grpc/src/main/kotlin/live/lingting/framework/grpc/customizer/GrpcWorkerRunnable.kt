package live.lingting.framework.grpc.customizer

import live.lingting.framework.thread.WorkerRunnable
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lingting 2024/12/23 18:31
 */
class GrpcWorkerRunnable(val r: Runnable) : WorkerRunnable() {

    companion object {
        private val step = AtomicLong()
    }

    private var oldName: String? = null

    val index = step.andIncrement

    override fun onStart() {
        val thread = Thread.currentThread()
        oldName = thread.name
        thread.name = "gw-${index}"
        GrpcThreadExecutorCustomizer.bind(this)
    }

    override fun onFinally() {
        oldName?.also { Thread.currentThread().name = it }
        GrpcThreadExecutorCustomizer.shutdown()
    }

}
