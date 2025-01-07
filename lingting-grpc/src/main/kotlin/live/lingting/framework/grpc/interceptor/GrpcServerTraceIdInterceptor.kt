package live.lingting.framework.grpc.interceptor

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import live.lingting.framework.Sequence
import live.lingting.framework.application.ApplicationHolder
import live.lingting.framework.grpc.properties.GrpcServerProperties
import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils

/**
 * 在服务器端，按照拦截器注册的顺序从后到前执行，先执行后面的拦截器，再执行前面的拦截器。
 * @author lingting 2023-04-13 13:23
 */
open class GrpcServerTraceIdInterceptor(val properties: GrpcServerProperties) : ServerInterceptor, Sequence {

    protected val log = logger()

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

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val traceId = traceId(headers)
        MdcUtils.setTraceId(traceId)
        try {
            // 返回traceId
            headers.put(traceIdKey, traceId)
            if (ApplicationHolder.isStop && properties.rejectRequestOnStop) {
                // 拒绝请求
                call.close(Status.UNAVAILABLE, null)
                log.warn("reject request. uri: {}", call.methodDescriptor?.fullMethodName)
                return object : ServerCall.Listener<ReqT>() {
                }
            }
            return next.startCall(call, headers)
        } finally {
            MdcUtils.removeTraceId()
        }
    }

    override val sequence: Int = properties.traceOrder

}
