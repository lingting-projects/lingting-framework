package live.lingting.polaris.grpc.server

import io.grpc.Context
import io.grpc.Contexts
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import live.lingting.polaris.grpc.metadata.MetadataContext

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class MetadataServerInterceptor : ServerInterceptor {
    override fun <R, P> interceptCall(
        serverCall: ServerCall<R, P>, metadata: Metadata,
        next: ServerCallHandler<R, P>
    ): ServerCall.Listener<R> {
        val newCtx = copyMetadataToMetadataContext(metadata)
        return Contexts.interceptCall(newCtx, serverCall, metadata, next)
    }

    private fun copyMetadataToMetadataContext(headers: Metadata): Context {
        val metadataContext: MetadataContext = MetadataContext.Companion.METADATA_CONTEXT_KEY.get()
        metadataContext.reset()

        val keys = headers.keys()

        for (key in keys) {
            val `val` = headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))
            metadataContext.putHeaderFragment(key, `val`)
        }

        return Context.current().withValue<MetadataContext>(MetadataContext.Companion.METADATA_CONTEXT_KEY, metadataContext)
    }
}
