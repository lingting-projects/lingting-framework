package live.lingting.framework.grpc.interceptor

import io.grpc.ServerInterceptor
import live.lingting.framework.grpc.GrpcServer

/**
 * @author lingting 2024-01-31 10:12
 */
abstract class AbstractServerInterceptor : ServerInterceptor {

    var server: GrpcServer? = null

}
