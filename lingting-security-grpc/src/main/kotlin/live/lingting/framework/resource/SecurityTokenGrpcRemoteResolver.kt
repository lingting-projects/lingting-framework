package live.lingting.framework.resource

import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import live.lingting.framework.Sequence
import live.lingting.framework.context.ContextComponent
import live.lingting.framework.convert.SecurityGrpcConvert
import live.lingting.framework.exception.SecurityGrpcThrowing
import live.lingting.framework.interceptor.SecurityGrpcRemoteContent
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceBlockingStub
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.resolver.SecurityTokenResolver

/**
 * @author lingting 2023-12-18 16:30
 */
class SecurityTokenGrpcRemoteResolver(protected val channel: ManagedChannel, protected val convert: SecurityGrpcConvert) : SecurityTokenResolver, ContextComponent, Sequence {
    protected val blocking: SecurityGrpcAuthorizationServiceBlockingStub = SecurityGrpcAuthorizationServiceGrpc.newBlockingStub(channel)

    @Throws(Exception::class)
    protected fun resolveByRemote(token: SecurityToken?): SecurityGrpcAuthorization.AuthorizationVO {
        try {
            SecurityGrpcRemoteContent.Companion.put(token)
            return blocking.resolve(Empty.getDefaultInstance())
        } catch (e: Exception) {
            throw SecurityGrpcThrowing.Companion.convert(e)
        } finally {
            SecurityGrpcRemoteContent.Companion.pop()
        }
    }

    override fun isSupport(token: SecurityToken?): Boolean {
        return true
    }


    override fun resolver(token: SecurityToken): SecurityScope? {
        val authorizationVO = resolveByRemote(token)
        val vo = convert.toJava(authorizationVO)
        return convert.voToScope(vo)
    }

    override fun onApplicationStart() {
        //
    }

    override fun onApplicationStop() {
        channel.shutdown()
    }

    override val sequence: Int
        get() = Int.MAX_VALUE
}