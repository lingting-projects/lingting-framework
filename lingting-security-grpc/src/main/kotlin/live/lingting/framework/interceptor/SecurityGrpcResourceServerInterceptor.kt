package live.lingting.framework.interceptor

import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import java.util.function.Consumer
import live.lingting.framework.Sequence
import live.lingting.framework.exception.SecurityGrpcThrowing
import live.lingting.framework.grpc.interceptor.AbstractServerInterceptor
import live.lingting.framework.grpc.simple.ForwardingServerOnCallListener
import live.lingting.framework.security.authorize.SecurityAuthorize
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.resource.SecurityResourceService
import live.lingting.framework.util.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2023-12-14 16:28
 */
class SecurityGrpcResourceServerInterceptor(
    private val authorizationKey: Metadata.Key<String>, private val service: SecurityResourceService,
    private val authorize: SecurityAuthorize
) : AbstractServerInterceptor(), Sequence {
    override fun <S, R> interceptCall(
        call: ServerCall<S, R>, headers: Metadata,
        next: ServerCallHandler<S, R>
    ): ServerCall.Listener<S> {
        val scope = scope(headers)
        service.putScope(scope)

        val descriptor = call.methodDescriptor

        if (allowAuthority(headers, descriptor)) {
            validAuthority(descriptor)
        }
        return object : ForwardingServerOnCallListener<S, R>(call, headers, next) {
            override fun onFinally() {
                service.popScope()
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SecurityGrpcResourceServerInterceptor::class.java)
    }

    protected fun <S, R> validAuthority(descriptor: MethodDescriptor<S, R>) {
        val cls = server!!.findClass(descriptor)
        val method = server!!.findMethod(descriptor)
        authorize.valid(cls, method)
    }

    protected fun scope(metadata: Metadata): SecurityScope? {
        val token = token(metadata)
        // token有效, 设置上下文
        if (!token.isAvailable) {
            return null
        }

        try {
            return service.resolve(token)
        } catch (ex: Exception) {
            SecurityGrpcThrowing.throwing(ex, Consumer { e -> log.error("resolve token error! token: {}", token, e) })
            return null
        }
    }

    protected fun token(metadata: Metadata): SecurityToken {
        val raw = metadata.get(authorizationKey)
        if (!StringUtils.hasText(raw)) {
            return SecurityToken.EMPTY
        }
        return SecurityToken.ofDelimiter(raw!!, " ")
    }

    protected fun allowAuthority(metadata: Metadata, descriptor: MethodDescriptor<*, *>): Boolean {
        return true
    }

    override val sequence: Int = authorize.order


}
