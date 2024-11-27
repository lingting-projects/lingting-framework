package live.lingting.framework.exception

import io.grpc.MethodDescriptor
import io.grpc.Status
import live.lingting.framework.grpc.exception.GrpcExceptionHandler
import live.lingting.framework.grpc.exception.GrpcExceptionInstance
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.exception.PermissionsException
import live.lingting.framework.util.Slf4jUtils.logger

/**
 * @author lingting 2023-12-15 17:15
 */
class SecurityGrpcExceptionInstance : GrpcExceptionInstance {
    /**
     * 鉴权异常
     */
    @GrpcExceptionHandler(AuthorizationException::class)
    fun handlerAuthorizationException(descriptor: MethodDescriptor<*, *>, e: AuthorizationException): Status {
        log.error("Authorization error! target: {}. {}", descriptor.fullMethodName, e.message)
        return Status.UNAUTHENTICATED.withCause(e).withDescription(e.message)
    }

    /**
     * 权限异常
     */
    @GrpcExceptionHandler(PermissionsException::class)
    fun handlerPermissionsException(descriptor: MethodDescriptor<*, *>, e: PermissionsException): Status {
        log.error("Permissions error! target: {}. {}", descriptor.fullMethodName, e.message)
        return Status.PERMISSION_DENIED.withCause(e).withDescription(e.message)
    }

    companion object {
        private val log = logger()
    }
}
