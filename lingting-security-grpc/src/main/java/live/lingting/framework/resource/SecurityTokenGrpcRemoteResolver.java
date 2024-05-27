package live.lingting.framework.resource;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import live.lingting.framework.Sequence;
import live.lingting.framework.context.ContextComponent;
import live.lingting.framework.convert.SecurityGrpcConvert;
import live.lingting.framework.interceptor.SecurityGrpcRemoteContent;
import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.resolver.SecurityTokenResolver;
import live.lingting.protobuf.SecurityGrpcAuthorization;
import live.lingting.protobuf.SecurityGrpcAuthorizationServiceGrpc;
import lombok.SneakyThrows;

import static live.lingting.framework.exception.SecurityGrpcThrowing.convert;

/**
 * @author lingting 2023-12-18 16:30
 */
@SuppressWarnings("java:S112")
public class SecurityTokenGrpcRemoteResolver implements SecurityTokenResolver, ContextComponent, Sequence {

	protected final ManagedChannel channel;

	protected final SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceBlockingStub blocking;

	protected final SecurityGrpcConvert convert;

	public SecurityTokenGrpcRemoteResolver(ManagedChannel channel, SecurityGrpcConvert convert) {
		this.channel = channel;
		this.blocking = SecurityGrpcAuthorizationServiceGrpc.newBlockingStub(channel);
		this.convert = convert;
	}

	protected SecurityGrpcAuthorization.AuthorizationVO resolveByRemote(SecurityToken token) throws Exception {
		try {
			SecurityGrpcRemoteContent.put(token);
			return blocking.resolve(Empty.getDefaultInstance());
		}
		catch (Exception e) {
			throw convert(e);
		}
		finally {
			SecurityGrpcRemoteContent.pop();
		}
	}

	@Override
	public boolean isSupport(SecurityToken token) {
		return true;
	}

	@SneakyThrows
	@Override
	public SecurityScope resolver(SecurityToken token) {
		SecurityGrpcAuthorization.AuthorizationVO authorizationVO = resolveByRemote(token);
		AuthorizationVO vo = convert.toJava(authorizationVO);
		return convert.voToScope(vo);
	}

	@Override
	public void onApplicationStart() {
		//
	}

	@Override
	public void onApplicationStop() {
		channel.shutdown();
	}

	@Override
	public int getSequence() {
		return Integer.MAX_VALUE;
	}

}
