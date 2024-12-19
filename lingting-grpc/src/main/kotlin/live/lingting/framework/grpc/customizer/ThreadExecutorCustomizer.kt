package live.lingting.framework.grpc.customizer

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.ServerBuilder
import io.grpc.ServerCall
import io.grpc.ServerCallExecutorSupplier
import io.grpc.ServerInterceptor
import java.util.concurrent.Executor
import live.lingting.framework.thread.executor.CallPolicy
import live.lingting.framework.thread.executor.CallPolicyResolver
import live.lingting.framework.thread.executor.PolicyThreadPoolExecutor
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.ThreadUtils

/**
 * @author lingting 2024/12/19 22:44
 */
class ThreadExecutorCustomizer @JvmOverloads constructor(val executor: Executor = ThreadUtils.executor()) : ClientCustomizer, ServerCustomizer {

    companion object {
        @JvmStatic
        val resolver = CallPolicyResolver {
            if (it.javaClass.name == "io.grpc.internal.SerializingExecutor") {
                CallPolicy.DIRECT
            } else {
                null
            }
        }
    }

    val log = logger()

    fun register() {
        if (executor !is PolicyThreadPoolExecutor) {
            log.warn("This thread pool may cause context exceptions through ThreadLocal! Please use PolicyThreadPoolExecutor.")
            return
        }
        executor.register(resolver)
    }

    override fun customize(builder: ManagedChannelBuilder<*>): Collection<ClientInterceptor> {
        register()
        builder.executor(executor)
        builder.offloadExecutor(executor)
        return emptyList()
    }

    override fun customize(builder: ServerBuilder<*>): Collection<ServerInterceptor> {
        register()
        builder.executor(executor)
        builder.callExecutor(object : ServerCallExecutorSupplier {
            override fun <ReqT, RespT> getExecutor(call: ServerCall<ReqT, RespT>, metadata: Metadata): Executor? {
                return executor
            }
        })
        return emptyList()
    }
}
