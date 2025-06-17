package live.lingting.framework.grpc.customizer

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.ServerBuilder
import io.grpc.ServerCall
import io.grpc.ServerCallExecutorSupplier
import io.grpc.ServerInterceptor
import live.lingting.framework.grpc.interceptor.GrpcThreadExecutorInterceptor
import live.lingting.framework.thread.executor.DelegationExecutorService
import live.lingting.framework.thread.executor.PolicyExecutorService
import live.lingting.framework.thread.executor.ThreadExecuteResolver
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ThreadUtils.threadId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor

/**
 * @author lingting 2024/12/19 22:44
 */
open class GrpcThreadExecutorCustomizer @JvmOverloads constructor(val executor: Executor = ThreadUtils) :
    ClientCustomizer, ServerCustomizer, ThreadExecuteResolver {

    companion object {

        private val runnableMap = ConcurrentHashMap<Runnable, GrpcWorkerRunnable>()

        private val threadMap = ConcurrentHashMap<Long, GrpcWorkerRunnable>()

        @JvmStatic
        fun submit(command: Runnable): GrpcWorkerRunnable {
            return runnableMap.computeIfAbsent(command) {
                GrpcWorkerRunnable(it)
            }.also { it.push(command) }
        }

        @JvmStatic
        fun bind(runnable: GrpcWorkerRunnable, thread: Thread = Thread.currentThread()) {
            val id = thread.threadId()
            threadMap[id] = runnable
        }

        @JvmStatic
        @JvmOverloads
        fun shutdown(thread: Thread = Thread.currentThread()) {
            val id = thread.threadId()
            val runnable = threadMap.remove(id)
            runnable?.also {
                runnableMap.remove(it.r)
                it.interrupt()
            }
        }

    }

    val log = logger()

    fun register() {
        val target = executor as? PolicyExecutorService
            ?: if (executor is DelegationExecutorService) executor.find(PolicyExecutorService::class.java)
            else null
        if (target == null) {
            log.warn("This thread pool may cause context exceptions through ThreadLocal! Please use PolicyThreadPoolExecutor.")
            return
        }
        target.register(this)
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
        return submit(command)
    }

}
