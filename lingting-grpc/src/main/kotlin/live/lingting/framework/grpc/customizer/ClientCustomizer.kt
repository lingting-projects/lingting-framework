package live.lingting.framework.grpc.customizer

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannelBuilder

/**
 * @author lingting 2024/12/19 22:38
 */
fun interface ClientCustomizer {

    fun customize(builder: ManagedChannelBuilder<*>): Collection<ClientInterceptor>

}
