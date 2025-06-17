package live.lingting.framework.security.grpc.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import live.lingting.framework.Sequence
import live.lingting.framework.grpc.simple.ForwardingClientOnCall
import live.lingting.framework.grpc.simple.ForwardingClientOnCallListener
import live.lingting.framework.util.Slf4jUtils.logger

/**
 * @author lingting 2024/12/19 20:13
 */
class GzipInterceptor : ServerInterceptor, ClientInterceptor, Sequence {
    val log = logger()

    val grpcEncoding = Metadata.Key.of("grpc-encoding", Metadata.ASCII_STRING_MARSHALLER)
    val grpcAcceptEncoding = Metadata.Key.of("grpc-accept-encoding", Metadata.ASCII_STRING_MARSHALLER)

    override fun <ReqT : Any, RespT : Any> interceptCall(call: ServerCall<ReqT, RespT>, headers: Metadata, next: ServerCallHandler<ReqT, RespT>): ServerCall.Listener<ReqT> {
        logHeader(headers, true)
        return next.startCall(call, headers)
    }

    fun logHeader(headers: Metadata, server: Boolean) {
        try {
            val tag = if (server) "Server" else "Client"
            log.info("[{}] grpcEncoding: {}", tag, headers.get(grpcEncoding))
            log.info("[{}] grpcAcceptEncoding: {}", tag, headers.get(grpcAcceptEncoding))
        } catch (e: Exception) {
            log.error("header get error", e)
        }
    }

    override fun <ReqT, RespT> interceptCall(method: MethodDescriptor<ReqT, RespT>, callOptions: CallOptions, next: Channel): ClientCall<ReqT, RespT> {
        return object : ForwardingClientOnCall<ReqT, RespT>(method, callOptions, next) {
            override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                val listen = object : ForwardingClientOnCallListener<RespT>(responseListener) {
                    override fun onHeadersAfter(headers: Metadata) {
                        logHeader(headers, false)
                    }
                }
                super.start(listen, headers)
            }
        }
    }

    override val sequence: Int = Int.MAX_VALUE
}
