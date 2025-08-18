package live.lingting.framework.grpc.interceptor

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
import live.lingting.framework.datascope.rule.DataScopeRuleHolder
import live.lingting.framework.grpc.simple.ForwardingClientOnCall
import live.lingting.framework.grpc.simple.ForwardingServerOnCallListener

/**
 * @author lingting 2025/8/18 16:48
 */
class GrpcDataScopeInterceptor : ClientInterceptor, ServerInterceptor, Sequence {

    override fun <S, R> interceptCall(
        method: MethodDescriptor<S, R>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<S, R> {

        return object : ForwardingClientOnCall<S, R>(method, callOptions, next) {
            override fun onFinally() {
                DataScopeRuleHolder.clear()
            }
        }
    }

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        return object : ForwardingServerOnCallListener<ReqT, RespT>(call, headers, next) {
            override fun onFinally() {
                DataScopeRuleHolder.clear()
            }
        }
    }

    override val sequence: Int = Int.MIN_VALUE

}
