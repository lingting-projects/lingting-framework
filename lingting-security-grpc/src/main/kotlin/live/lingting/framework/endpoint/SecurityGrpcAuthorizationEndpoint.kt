package live.lingting.framework.endpoint

import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import live.lingting.framework.convert.SecurityGrpcConvert
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceImplBase
import live.lingting.framework.security.SecurityEndpointService
import live.lingting.framework.security.annotation.Authorize
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.po.EndpointPasswordPO
import live.lingting.framework.security.po.EndpointTokenPO

/**
 * @author lingting 2023-12-18 15:31
 */
class SecurityGrpcAuthorizationEndpoint(
    val service: SecurityEndpointService,
    val convert: SecurityGrpcConvert,
) : SecurityGrpcAuthorizationServiceImplBase() {
    override fun logout(request: Empty, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val logout = service.logout()
        onNext(logout, observer)
    }

    @Authorize(anyone = true)
    override fun password(
        po: SecurityGrpcAuthorization.AuthorizationPasswordPO,
        observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>
    ) {
        val p = EndpointPasswordPO().apply {
            username = po.username
            password = po.password
        }
        val vo = service.password(p)
        onNext(vo, observer)
    }


    override fun refresh(request: SecurityGrpcAuthorization.TokenPO, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val p = EndpointTokenPO().apply {
            raw = request.raw
            type = request.type
            value = request.value
        }
        val vo = service.refresh(p)
        onNext(vo, observer)
    }

    @Authorize(anyone = true)
    override fun resolve(request: SecurityGrpcAuthorization.TokenPO, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val p = EndpointTokenPO().apply {
            raw = request.raw
            type = request.type
            value = request.value
        }
        val vo = service.resolve(p)
        onNext(vo, observer)
    }

    fun onNext(vo: AuthorizationVO, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val authorizationVO = convert.toProtobuf(vo)
        observer.onNext(authorizationVO)
        observer.onCompleted()
    }

}
