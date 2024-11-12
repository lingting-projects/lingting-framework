package live.lingting.framework.grpc.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import live.lingting.framework.Sequence
import live.lingting.framework.grpc.properties.GrpcClientProperties
import live.lingting.framework.grpc.simple.ForwardingClientOnCall
import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2023-04-13 13:23
 */
class GrpcClientTraceIdInterceptor(properties: GrpcClientProperties) : ClientInterceptor, Sequence {
    private val properties: GrpcClientProperties? = properties

    private val traceIdKey: Metadata.Key<String?> = Metadata.Key.of(properties.traceIdKey, Metadata.ASCII_STRING_MARSHALLER)

    /**
     * 获取当前上下文的traceId
     */
    protected fun traceId(): String? {
        return MdcUtils.getTraceId()
    }

    override fun <S, R> interceptCall(method: MethodDescriptor<S?, R?>?, callOptions: CallOptions?, next: Channel?): ClientCall<S?, R?> {
        val traceId = traceId()

        return object : ForwardingClientOnCall<S?, R?>(method, callOptions, next!!) {
            override fun onStartBefore(responseListener: Listener<R?>?, headers: Metadata?) {
                if (StringUtils.hasText(traceId)) {
                    headers!!.put(traceIdKey, traceId)
                }
            }
        }
    }

    override val sequence: Int
        get() = properties.getTraceOrder()
}
