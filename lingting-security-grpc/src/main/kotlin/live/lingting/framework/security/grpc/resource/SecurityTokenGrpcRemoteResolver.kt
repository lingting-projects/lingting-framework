package live.lingting.framework.security.grpc.resource

import io.grpc.ManagedChannel
import live.lingting.framework.Sequence
import live.lingting.framework.application.ApplicationComponent
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceBlockingStub
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.grpc.convert.SecurityGrpcConvert
import live.lingting.framework.security.grpc.exception.SecurityGrpcThrowing
import live.lingting.framework.security.resolver.SecurityTokenResolver

/**
 * @author lingting 2023-12-18 16:30
 */
open class SecurityTokenGrpcRemoteResolver(
    protected val channel: ManagedChannel,
    protected val convert: SecurityGrpcConvert,
) : SecurityTokenResolver, ApplicationComponent, Sequence {
    protected val blocking: SecurityGrpcAuthorizationServiceBlockingStub = SecurityGrpcAuthorizationServiceGrpc.newBlockingStub(channel)

    protected fun resolveByRemote(token: SecurityToken?): SecurityGrpcAuthorization.AuthorizationVO {
        try {
            val request = SecurityGrpcAuthorization.TokenPO.newBuilder()
                .setRaw(token?.raw)
                .setType(token?.type)
                .setValue(token?.value)
                .buildPartial()
            return blocking.resolve(request)
        } catch (e: Exception) {
            throw SecurityGrpcThrowing.convert(e)
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
