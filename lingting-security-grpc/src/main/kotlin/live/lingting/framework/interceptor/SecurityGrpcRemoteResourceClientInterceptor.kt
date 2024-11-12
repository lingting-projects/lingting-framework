package live.lingting.framework.interceptor

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import live.lingting.framework.grpc.simple.ForwardingClientOnCall
import live.lingting.framework.properties.SecurityGrpcProperties
import live.lingting.framework.security.domain.SecurityToken

/**
 * @author lingting 2023-12-18 16:37
 */
class SecurityGrpcRemoteResourceClientInterceptor(properties: SecurityGrpcProperties) : ClientInterceptor {
    private val authorizationKey: Metadata.Key<String?> = properties.authorizationKey()

    override fun <S, R> interceptCall(method: MethodDescriptor<S, R>, callOptions: CallOptions, next: Channel): ClientCall<S, R> {
        return object : ForwardingClientOnCall<S, R>(method, callOptions, next) {
            override fun onStartBefore(responseListener: Listener<R?>?, headers: Metadata?) {
                val securityToken: SecurityToken = SecurityGrpcRemoteContent.Companion.get()
                if (securityToken != null && securityToken.isAvailable) {
                    headers!!.put(authorizationKey, securityToken.raw)
                }
            }
        }
    }
}
