package live.lingting.framework.grpc.interceptor

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import live.lingting.framework.Sequence
import live.lingting.framework.grpc.exception.GrpcExceptionProcessor
import live.lingting.framework.grpc.properties.GrpcServerProperties

/**
 * @author lingting 2024-03-27 10:05
 */
class GrpcServerExceptionInterceptor(
    val properties: GrpcServerProperties,
    val processor: GrpcExceptionProcessor
) : ServerInterceptor, Sequence {
    override fun <S, R> interceptCall(
        call: ServerCall<S, R>, headers: Metadata,
        next: ServerCallHandler<S, R>
    ): ServerCall.Listener<S> {
        var listener: ServerCall.Listener<S>
        try {
            val nextCall = next.startCall(call, headers)
            listener = object : SimpleForwardingServerCallListener<S>(nextCall) {
                override fun onHalfClose() {
                    try {
                        super.onHalfClose()
                    } catch (e: Exception) {
                        process(e, call, headers)
                    }
                }
            }
        } catch (e: Exception) {
            process(e, call, headers)
            listener = object : ServerCall.Listener<S>() {
            }
        }
        return listener
    }

    override val sequence: Int = properties.exceptionHandlerOrder

    fun process(e: Exception, call: ServerCall<*, *>, headers: Metadata) {
        val invoke = processor.find(e)
        val obj = invoke.invoke(e, call, headers)
        if (obj is Status) {
            call.close(obj, headers)
        }
    }
}
