package live.lingting.framework.grpc.interceptor

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import live.lingting.framework.Sequence

/**
 * @author lingting 2024/12/19 21:54
 */
class GrpcServerCompressionInterceptor private constructor(val compressor: String) : ServerInterceptor, Sequence {

    companion object {

        @JvmStatic
        fun create(compressor: String): GrpcServerCompressionInterceptor {
            return GrpcServerCompressionInterceptor(compressor)
        }

    }

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata, next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        call.setCompression(compressor)
        return next.startCall(call, headers)
    }

    override val sequence: Int = Int.MIN_VALUE

}
