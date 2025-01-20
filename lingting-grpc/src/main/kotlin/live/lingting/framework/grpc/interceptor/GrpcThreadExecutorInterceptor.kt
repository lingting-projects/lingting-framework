package live.lingting.framework.grpc.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.MethodDescriptor
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import live.lingting.framework.Sequence
import live.lingting.framework.grpc.customizer.GrpcThreadExecutorCustomizer
import live.lingting.framework.grpc.simple.ForwardingClientOnCall
import live.lingting.framework.grpc.simple.ForwardingServerOnCallListener
import live.lingting.framework.util.Slf4jUtils.logger

/**
 * @author lingting 2024/12/23 18:32
 */
object GrpcThreadExecutorInterceptor : ClientInterceptor, ServerInterceptor, Sequence {

    val log = logger()

    override fun <ReqT : Any, RespT : Any> interceptCall(call: ServerCall<ReqT, RespT>, headers: io.grpc.Metadata, next: ServerCallHandler<ReqT, RespT>):
            ServerCall.Listener<ReqT> {
        return object : ForwardingServerOnCallListener<ReqT, RespT>(call, headers, next) {
            override fun onFinally() {
                GrpcThreadExecutorCustomizer.shutdown()
            }
        }
    }

    override fun <ReqT, RespT> interceptCall(method: MethodDescriptor<ReqT, RespT>, callOptions: CallOptions, next: Channel): ClientCall<ReqT, RespT> {
        return object : ForwardingClientOnCall<ReqT, RespT>(method, callOptions, next) {
            override fun onFinally() {
                GrpcThreadExecutorCustomizer.shutdown()
            }
        }
    }

    override val sequence: Int = Int.MIN_VALUE + 1000

}
