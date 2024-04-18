package live.lingting.framework.resource;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import live.lingting.framework.context.ContextComponent;
import live.lingting.framework.convert.SecurityGrpcConvert;
import live.lingting.framework.interceptor.SecurityGrpcRemoteContent;
import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
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

	public SecurityGrpcDefaultRemoteResourceServiceImpl(ManagedChannel channel, SecurityGrpcConvert convert) {
		this.channel = channel;
		this.blocking = SecurityGrpcAuthorizationServiceGrpc.newBlockingStub(channel);
		this.convert = convert;
	}

	@Override
	public SecurityScope resolve(SecurityToken token) {
		SecurityGrpcAuthorization.AuthorizationVO authorizationVO = resolveByRemote(token);
		AuthorizationVO vo = convert.toJava(authorizationVO);
		return convert.voToScope(vo);
	}

	protected SecurityGrpcAuthorization.AuthorizationVO resolveByRemote(SecurityToken token) {
		try {
			SecurityGrpcRemoteContent.put(token);
			return blocking.resolve(Empty.getDefaultInstance());
		}
		finally {
			SecurityGrpcRemoteContent.pop();
		}
	}

	@Override
	public void onApplicationStart() {
		//
	}

	@Override
	public void onApplicationStop() {
		channel.shutdown();
	}

}
