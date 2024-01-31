package live.lingting.framework.resource;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import live.lingting.framework.context.ContextComponent;
import live.lingting.framework.convert.SecurityGrpcConvert;
import live.lingting.framework.grpc.client.GrpcClientProvide;
import live.lingting.framework.interceptor.SecurityGrpcRemoteResourceClientInterceptor;
import live.lingting.framework.properties.SecurityGrpcProperties;
import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.properties.SecurityProperties;
import live.lingting.framework.security.resource.SecurityResourceService;
import live.lingting.protobuf.SecurityGrpcAuthorization;
import live.lingting.protobuf.SecurityGrpcAuthorizationServiceGrpc;

/**
 * @author lingting 2023-12-18 16:30
 */
public class SecurityGrpcDefaultRemoteResourceServiceImpl implements SecurityResourceService, ContextComponent {

	protected final ManagedChannel channel;

	protected final SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceBlockingStub blocking;

	protected final SecurityGrpcConvert convert;

	public SecurityGrpcDefaultRemoteResourceServiceImpl(SecurityProperties properties,
														SecurityGrpcProperties grpcProperties, GrpcClientProvide provide, SecurityGrpcConvert convert) {
		SecurityProperties.Authorization authorization = properties.getAuthorization();
		channel = provide.builder(authorization.getRemoteHost())
			.provide()
			.interceptor(new SecurityGrpcRemoteResourceClientInterceptor(grpcProperties))
			.build();
		blocking = provide.stub(channel, SecurityGrpcAuthorizationServiceGrpc::newBlockingStub);
		this.convert = convert;
	}

	@Override
	public SecurityScope resolve(SecurityToken token) {
		try {
			SecurityTokenHolder.set(token);
			SecurityGrpcAuthorization.AuthorizationVO authorizationVO = blocking.resolve(Empty.getDefaultInstance());
			AuthorizationVO vo = convert.toJava(authorizationVO);
			return convert.voToScope(vo);
		}
		finally {
			SecurityTokenHolder.remove();
		}
	}

	@Override
	public void onApplicationStart() {
		//
	}

	@Override
	public void onApplicationStop() {
		channel.shutdownNow();
	}

}
