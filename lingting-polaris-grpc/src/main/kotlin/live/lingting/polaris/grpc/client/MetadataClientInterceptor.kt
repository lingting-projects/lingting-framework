package live.lingting.polaris.grpc.client

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import live.lingting.polaris.grpc.metadata.MetadataContext

import java.util.function.Predicate

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class MetadataClientInterceptor(transitiveFilter: Predicate<String?>) : ClientInterceptor {
    private val transitiveFilter: Predicate<String?>

    init {
        Objects.requireNonNull(transitiveFilter, "transitiveFilter must not be null")
        this.transitiveFilter = transitiveFilter
    }

    override fun <R, P> interceptCall(
        methodDescriptor: MethodDescriptor<R, P>, callOptions: CallOptions,
        channel: Channel
    ): ClientCall<R, P> {
        return object : SimpleForwardingClientCall<R, P>(channel.newCall(methodDescriptor, callOptions)) {
            override fun start(responseListener: Listener<P>, headers: Metadata) {
                copyMetadataToHeader(headers)
                super.start(responseListener, headers)
            }
        }
    }

    private fun copyMetadataToHeader(headers: Metadata) {
        val metadataContext: MetadataContext = MetadataContext.Companion.METADATA_CONTEXT_KEY.get()

        metadataContext.headerFragment.forEach { (key: String?, `val`: String?) ->
            if (!transitiveFilter.test(key)) {
                return@forEach
            }
            headers.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), `val`)
        }

        metadataContext.grpcContextFragment.forEach { (key: String?, `val`: String?) ->
            if (!transitiveFilter.test(key)) {
                return@forEach
            }
            headers.put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), `val`)
        }
    }
}
