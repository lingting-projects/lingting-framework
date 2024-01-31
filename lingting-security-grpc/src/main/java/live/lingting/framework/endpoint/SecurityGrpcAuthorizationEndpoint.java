package live.lingting.framework.endpoint;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import live.lingting.framework.convert.SecurityGrpcConvert;
import live.lingting.framework.security.annotation.Authorize;
import live.lingting.framework.security.authorize.SecurityAuthorizationService;
import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.exception.AuthorizationException;
import live.lingting.framework.security.password.SecurityPassword;
import live.lingting.framework.security.resource.SecurityHolder;
import live.lingting.framework.security.store.SecurityStore;
import live.lingting.protobuf.SecurityGrpcAuthorization;
import live.lingting.protobuf.SecurityGrpcAuthorizationServiceGrpc;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2023-12-18 15:31
 */
@RequiredArgsConstructor
public class SecurityGrpcAuthorizationEndpoint
	extends SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceImplBase {

	private final SecurityAuthorizationService service;

	private final SecurityStore store;

	private final SecurityPassword securityPassword;

	private final SecurityGrpcConvert convert;

	@Override
	public void logout(Empty request, StreamObserver<SecurityGrpcAuthorization.AuthorizationVO> observer) {
		SecurityScope scope = SecurityHolder.scope();
		store.deleted(scope);
		onNext(scope, observer);
	}

	@Override
	@Authorize(anyone = true)
	public void password(SecurityGrpcAuthorization.AuthorizationPasswordPO po,
						 StreamObserver<SecurityGrpcAuthorization.AuthorizationVO> observer) {
		String username = po.getUsername();
		String rawPassword = po.getPassword();
		String password = securityPassword.decodeFront(rawPassword);
		SecurityScope rawScope = service.validAndBuildScope(username, password);
		if (rawScope == null) {
			throw new AuthorizationException("Username or password is incorrect!");
		}
		SecurityScope scope = convert.scopeExpand(rawScope);
		store.save(scope);
		onNext(scope, observer);
	}

	@Override
	public void refresh(Empty request, StreamObserver<SecurityGrpcAuthorization.AuthorizationVO> observer) {
		SecurityScope scope = service.refresh(SecurityHolder.token());
		if (scope == null) {
			throw new AuthorizationException("Authorization has expired!");
		}
		store.update(scope);
		onNext(scope, observer);
	}

	@Override
	public void resolve(Empty request, StreamObserver<SecurityGrpcAuthorization.AuthorizationVO> observer) {
		SecurityScope scope = SecurityHolder.scope();
		onNext(scope, observer);
	}

	protected void onNext(SecurityScope scope, StreamObserver<SecurityGrpcAuthorization.AuthorizationVO> observer) {
		AuthorizationVO vo = convert.scopeToVo(scope);
		onNext(vo, observer);
	}

	protected void onNext(AuthorizationVO vo, StreamObserver<SecurityGrpcAuthorization.AuthorizationVO> observer) {
		SecurityGrpcAuthorization.AuthorizationVO authorizationVO = convert.toProtobuf(vo);
		observer.onNext(authorizationVO);
		observer.onCompleted();
	}

}
