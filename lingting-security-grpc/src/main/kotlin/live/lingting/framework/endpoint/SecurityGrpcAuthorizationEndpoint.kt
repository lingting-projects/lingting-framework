package live.lingting.framework.endpoint

import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver
import live.lingting.framework.convert.SecurityGrpcConvert
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceImplBase
import live.lingting.framework.security.annotation.Authorize
import live.lingting.framework.security.authorize.SecurityAuthorizationService
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.password.SecurityPassword
import live.lingting.framework.security.resource.SecurityHolder.scope
import live.lingting.framework.security.resource.SecurityHolder.token
import live.lingting.framework.security.store.SecurityStore

/**
 * @author lingting 2023-12-18 15:31
 */
class SecurityGrpcAuthorizationEndpoint
    (private val service: SecurityAuthorizationService, private val store: SecurityStore, private val securityPassword: SecurityPassword, private val convert: SecurityGrpcConvert) : SecurityGrpcAuthorizationServiceImplBase() {
    override fun logout(request: Empty, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val scope = scope()
        store.deleted(scope!!)
        onNext(scope, observer)
    }

    @Authorize(anyone = true)
    override fun password(
        po: SecurityGrpcAuthorization.AuthorizationPasswordPO,
        observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>
    ) {
        val username = po.username
        val rawPassword = po.password
        val password = securityPassword.decodeFront(rawPassword)
        val rawScope = service.validAndBuildScope(username, password) ?: throw AuthorizationException("Username or password is incorrect!")
        val scope = convert.scopeExpand(rawScope)
        store.save(scope)
        onNext(scope, observer)
    }

    override fun refresh(request: SecurityGrpcAuthorization.TokenPO, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val token = token()
        val scope = service.refresh(token) ?: throw AuthorizationException("Authorization has expired!")
        store.update(scope)
        onNext(scope, observer)
    }

    @Authorize(anyone = true)
    override fun resolve(request: SecurityGrpcAuthorization.TokenPO, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val value = request.value
        val scope = store.get(value)
        if (scope == null || !scope.isLogin || !scope.enabled()) {
            throw AuthorizationException("Token is invalid!")
        }
        onNext(scope, observer)
    }

    protected fun onNext(scope: SecurityScope?, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val vo = convert.scopeToVo(scope)
        onNext(vo, observer)
    }

    protected fun onNext(vo: AuthorizationVO, observer: StreamObserver<SecurityGrpcAuthorization.AuthorizationVO>) {
        val authorizationVO = convert.toProtobuf(vo)
        observer.onNext(authorizationVO)
        observer.onCompleted()
    }
}
