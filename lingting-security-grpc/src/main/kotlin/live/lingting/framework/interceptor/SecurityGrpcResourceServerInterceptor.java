package live.lingting.framework.interceptor;

import io.grpc.BindableService;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import live.lingting.framework.Sequence;
import live.lingting.framework.grpc.interceptor.AbstractServerInterceptor;
import live.lingting.framework.grpc.simple.ForwardingServerOnCallListener;
import live.lingting.framework.security.authorize.SecurityAuthorize;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.resource.SecurityResourceService;
import live.lingting.framework.util.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Method;

import static live.lingting.framework.exception.SecurityGrpcThrowing.throwing;

/**
 * @author lingting 2023-12-14 16:28
 */
@SuppressWarnings("java:S1172")
public class SecurityGrpcResourceServerInterceptor extends AbstractServerInterceptor implements Sequence {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(SecurityGrpcResourceServerInterceptor.class);
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
		SecurityScope scope = getScope(headers);
		service.putScope(scope);

		MethodDescriptor<S, R> descriptor = call.getMethodDescriptor();

		if (allowAuthority(headers, descriptor)) {
			validAuthority(descriptor);
		}
		return new ForwardingServerOnCallListener<>(call, headers, next) {
			@Override
			public void onFinally() {
				service.popScope();
			}
		};
	}

	protected <S, R> void validAuthority(MethodDescriptor<S, R> descriptor) {
		Class<? extends BindableService> cls = server.findClass(descriptor);
		Method method = server.findMethod(descriptor);
		authorize.valid(cls, method);
	}

	protected SecurityScope getScope(Metadata metadata) {
		SecurityToken token = getToken(metadata);
		// token有效, 设置上下文
		if (!token.isAvailable()) {
			return null;
		}

		try {
			return service.resolve(token);
		}
		catch (Exception ex) {
			throwing(ex, e -> log.error("resolve token error! token: {}", token, e));
			return null;
		}
	}

	protected SecurityToken getToken(Metadata metadata) {
		String raw = metadata.get(authorizationKey);
		if (!StringUtils.hasText(raw)) {
			return SecurityToken.EMPTY;
		}
		return SecurityToken.ofDelimiter(raw, " ");
	}

	protected boolean allowAuthority(Metadata metadata, MethodDescriptor<?, ?> descriptor) {
		return true;
	}

	@Override
	public int getSequence() {
		return authorize.getOrder();
	}

}
