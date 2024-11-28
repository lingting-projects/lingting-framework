package live.lingting.framework.security.grpc.properties

import io.grpc.Metadata

/**
 * @author lingting 2023-12-14 16:40
 */
class SecurityGrpcProperties {
    var authorizationKey: String = "Authorization"

    fun authorizationKey(): Metadata.Key<String> {
        return Metadata.Key.of(authorizationKey, Metadata.ASCII_STRING_MARSHALLER)
    }
}
