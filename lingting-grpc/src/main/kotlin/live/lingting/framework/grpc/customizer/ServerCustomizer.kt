package live.lingting.framework.grpc.customizer

import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor

/**
 * @author lingting 2024/12/19 22:39
 */
fun interface ServerCustomizer {

    fun customize(builder: ServerBuilder<*>): Collection<ServerInterceptor>

}
