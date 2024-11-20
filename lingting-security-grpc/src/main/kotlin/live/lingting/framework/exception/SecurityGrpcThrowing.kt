package live.lingting.framework.exception

import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import java.util.function.Consumer
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.exception.PermissionsException

/**
 * @author lingting 2024-04-18 16:24
 */
object SecurityGrpcThrowing {
    @JvmStatic
    fun convert(e: Exception): Exception {
        if (e is StatusException || e is StatusRuntimeException) {
            val status = if (e is StatusException) e.status else (e as StatusRuntimeException).status
            when (status.code) {
                Status.Code.UNAUTHENTICATED -> return AuthorizationException(status.description, e)
                Status.Code.PERMISSION_DENIED -> return PermissionsException(status.description, e)
                else -> {}
            }
        }
        return e
    }

    @JvmStatic
    fun throwing(ex: Exception, consumer: Consumer<Exception>) {
        val e = convert(ex)
        if (e is AuthorizationException) {
            throw e
        }
        if (e is PermissionsException) {
            throw e
        }

        consumer.accept(e)
    }
}
