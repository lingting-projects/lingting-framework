package live.lingting.framework.grpc.interceptor

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import live.lingting.framework.Sequence
import live.lingting.framework.grpc.properties.GrpcServerProperties
import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.StringUtils

/**
 * 在服务器端，按照拦截器注册的顺序从后到前执行，先执行后面的拦截器，再执行前面的拦截器。
 * @author lingting 2023-04-13 13:23
 */
open class GrpcServerTraceIdInterceptor(val properties: GrpcServerProperties) : ServerInterceptor, Sequence {

    private val traceIdKey: Metadata.Key<String> = Metadata.Key.of(properties.traceIdKey, Metadata.ASCII_STRING_MARSHALLER)

    /**
     * 从请求中获取traceId, 如果没有返回生成的traceId
     */
    protected fun traceId(headers: Metadata): String {
        var traceId: String? = null
        if (headers.containsKey(traceIdKey)) {
            traceId = headers[traceIdKey]
        }
        if (StringUtils.hasText(traceId)) {
            return traceId!!
        }
        return MdcUtils.traceId()
    }

    override fun <S, R> interceptCall(
        call: ServerCall<S, R>, headers: Metadata,
        next: ServerCallHandler<S, R>
    ): ServerCall.Listener<S> {
        val traceId = traceId(headers)
        MdcUtils.setTraceId(traceId)
        try {
            // 返回traceId
            headers.put(traceIdKey, traceId)
            return next.startCall(call, headers)
        } finally {
            MdcUtils.removeTraceId()
        }
    }

    override val sequence: Int = properties.traceOrder

}
