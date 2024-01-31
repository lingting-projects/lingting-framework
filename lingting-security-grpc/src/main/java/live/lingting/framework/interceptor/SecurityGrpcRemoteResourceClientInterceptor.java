package live.lingting.framework.interceptor;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import live.lingting.framework.grpc.client.simple.ForwardingClientOnCall;
import live.lingting.framework.properties.SecurityGrpcProperties;
import live.lingting.framework.resource.SecurityTokenHolder;
import live.lingting.framework.security.domain.SecurityToken;

/**
 * @author lingting 2023-12-18 16:37
 */
@SuppressWarnings("java:S110")
public class SecurityGrpcRemoteResourceClientInterceptor implements ClientInterceptor {

	private final Metadata.Key<String> authorizationKey;

	public SecurityGrpcRemoteResourceClientInterceptor(SecurityGrpcProperties properties) {
		this.authorizationKey = properties.authorizationKey();
	}

	@Override
	public <S, R> ClientCall<S, R> interceptCall(MethodDescriptor<S, R> method, CallOptions callOptions, Channel next) {
		ClientCall<S, R> call = next.newCall(method, callOptions);
		return new ForwardingClientOnCall<>(call) {
			@Override
			public void onStartBefore(Listener<R> responseListener, Metadata headers) {
				SecurityToken token = SecurityTokenHolder.get();
				if (token != null && token.isAvailable()) {
					headers.put(authorizationKey, token.getRaw());
				}
			}
		};
	}

}
