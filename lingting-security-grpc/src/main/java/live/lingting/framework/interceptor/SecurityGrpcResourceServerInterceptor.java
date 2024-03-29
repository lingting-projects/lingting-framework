package live.lingting.framework.interceptor;

import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import live.lingting.framework.Sequence;
import live.lingting.framework.grpc.interceptor.AbstractServerInterceptor;
import live.lingting.framework.security.annotation.Authorize;
import live.lingting.framework.security.authorize.SecurityAuthorize;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.resource.SecurityResourceService;
import live.lingting.framework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingting 2023-12-14 16:28
 */
@Slf4j
@SuppressWarnings("java:S1172")
public class SecurityGrpcResourceServerInterceptor extends AbstractServerInterceptor implements Sequence {

	private final Metadata.Key<String> authorizationKey;

	private final SecurityResourceService service;

	private final SecurityAuthorize authorize;

	public SecurityGrpcResourceServerInterceptor(Metadata.Key<String> authorizationKey, SecurityResourceService service,
			SecurityAuthorize authorize) {
		this.authorizationKey = authorizationKey;
		this.service = service;
		this.authorize = authorize;
	}

	@Override
	public <S, R> ServerCall.Listener<S> interceptCall(ServerCall<S, R> call, Metadata headers,
			ServerCallHandler<S, R> next) {
		handlerScope(headers);

		MethodDescriptor<S, R> descriptor = call.getMethodDescriptor();

		if (allowAuthority(headers, descriptor)) {
			Authorize annotation = getAuthorize(headers, descriptor);
			authorize.valid(annotation);
		}

		return next.startCall(call, headers);
	}

	protected void handlerScope(Metadata metadata) {
		SecurityToken token = getToken(metadata);
		// token有效, 设置上下文
		if (!token.isAvailable()) {
			return;
		}

		try {
			SecurityScope scope = service.resolve(token);
			service.putScope(scope);
		}
		catch (Exception e) {
			log.error("resolve token error! token: {}", token, e);
		}
	}

	protected SecurityToken getToken(Metadata metadata) {
		String raw = metadata.get(authorizationKey);
		if (!StringUtils.hasText(raw)) {
			return SecurityToken.EMPTY;
		}
		return SecurityToken.ofDelimiter(raw, " ");
	}

	protected Authorize getAuthorize(Metadata metadata, MethodDescriptor<?, ?> descriptor) {
		Authorize methodAuthorize = server.findMethod(descriptor).getAnnotation(Authorize.class);
		if (methodAuthorize != null) {
			return methodAuthorize;
		}
		return server.findClass(descriptor).getAnnotation(Authorize.class);
	}

	protected boolean allowAuthority(Metadata metadata, MethodDescriptor<?, ?> descriptor) {
		return true;
	}

	@Override
	public int getSequence() {
		return authorize.getOrder();
	}

}
