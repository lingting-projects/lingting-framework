package live.lingting.framework.grpc.customizer

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.ServerBuilder
import io.grpc.ServerCall
import io.grpc.ServerCallExecutorSupplier
import io.grpc.ServerInterceptor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import live.lingting.framework.grpc.interceptor.GrpcThreadExecutorInterceptor
import live.lingting.framework.thread.executor.PolicyThreadPoolExecutor
import live.lingting.framework.thread.executor.ThreadExecuteResolver
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.ThreadUtils

/**
 * @author lingting 2024/12/19 22:44
 */
open class GrpcThreadExecutorCustomizer @JvmOverloads constructor(val executor: Executor = ThreadUtils.executor()) :
    ClientCustomizer, ServerCustomizer, ThreadExecuteResolver {

    companion object {
        @JvmField
        val RUNNABLE_MAP = ConcurrentHashMap<Runnable, GrpcWorkerRunnable>()

        @JvmField
        val THREAD_MAP = ConcurrentHashMap<Thread, GrpcWorkerRunnable>()
    }

    val log = logger()

    fun register() {
        if (executor !is PolicyThreadPoolExecutor) {
            log.warn("This thread pool may cause context exceptions through ThreadLocal! Please use PolicyThreadPoolExecutor.")
            return
        }
        executor.register(this)
    }

    override fun customize(builder: ManagedChannelBuilder<*>): Collection<ClientInterceptor> {
        register()
        builder.executor(executor)
        builder.offloadExecutor(executor)
        return listOf(GrpcThreadExecutorInterceptor)
    }

    override fun customize(builder: ServerBuilder<*>): Collection<ServerInterceptor> {
        register()
        builder.executor(executor)
        builder.callExecutor(object : ServerCallExecutorSupplier {
            override fun <ReqT, RespT> getExecutor(call: ServerCall<ReqT, RespT>, metadata: Metadata): Executor? {
                return executor
            }
        })
        return listOf(GrpcThreadExecutorInterceptor)
    }

    override fun isSupport(command: Runnable): Boolean {
        return command.javaClass.name == "io.grpc.internal.SerializingExecutor"
    }

    override fun wrapper(command: Runnable): Runnable? {
        return RUNNABLE_MAP.computeIfAbsent(command) { GrpcWorkerRunnable(it) }
            .also { it.push(command) }
    }

}
