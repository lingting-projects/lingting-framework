package live.lingting.framework.grpc.customizer

import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.Runnable
import live.lingting.framework.thread.WorkerRunnable

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
        GrpcThreadExecutorCustomizer.THREAD_MAP[thread] = this
        oldName = thread.name
        thread.name = "grpc-worker-${index}"
    }

    override fun onFinally() {
        oldName?.run { Thread.currentThread().name = this }
        GrpcThreadExecutorCustomizer.THREAD_MAP.remove(Thread.currentThread())
        GrpcThreadExecutorCustomizer.RUNNABLE_MAP.remove(r)
    }

}
