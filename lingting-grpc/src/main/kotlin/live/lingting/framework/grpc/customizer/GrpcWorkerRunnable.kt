package live.lingting.framework.grpc.customizer

import kotlinx.coroutines.Runnable
import live.lingting.framework.thread.WorkerRunnable

/**
 * @author lingting 2024/12/23 18:31
 */
class GrpcWorkerRunnable(val r: Runnable) : WorkerRunnable() {

    override fun run() {
        GrpcThreadExecutorCustomizer.THREAD_MAP[Thread.currentThread()] = this
        super.run()
    }

    override fun stop() {
        super.stop()
        GrpcThreadExecutorCustomizer.THREAD_MAP.remove(Thread.currentThread())
        GrpcThreadExecutorCustomizer.RUNNABLE_MAP.remove(r)
    }

}
