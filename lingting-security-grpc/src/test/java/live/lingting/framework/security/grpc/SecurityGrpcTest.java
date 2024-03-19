package live.lingting.framework.security.grpc;

import com.google.protobuf.Empty;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import live.lingting.framework.endpoint.SecurityGrpcAuthorizationEndpoint;
import live.lingting.framework.exception.SecurityGrpcExceptionHandler;
import live.lingting.framework.grpc.GrpcClientProvide;
import live.lingting.framework.grpc.GrpcServer;
import live.lingting.framework.grpc.GrpcServerBuilder;
import live.lingting.framework.grpc.interceptor.GrpcClientTraceIdInterceptor;
import live.lingting.framework.grpc.interceptor.GrpcServerTraceIdInterceptor;
import live.lingting.framework.grpc.properties.GrpcClientProperties;
import live.lingting.framework.grpc.properties.GrpcServerProperties;
import live.lingting.framework.interceptor.SecurityGrpcRemoteResourceClientInterceptor;
import live.lingting.framework.interceptor.SecurityGrpcResourceServerInterceptor;
import live.lingting.framework.properties.SecurityGrpcProperties;
import live.lingting.framework.resource.SecurityTokenHolder;
import live.lingting.framework.security.authorize.SecurityAuthorize;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.grpc.authorization.AuthorizationServiceImpl;
import live.lingting.framework.security.grpc.authorization.Password;
import live.lingting.framework.security.resource.SecurityDefaultResourceServiceImpl;
import live.lingting.framework.security.store.SecurityMemoryStore;
import live.lingting.framework.util.ValueUtils;
import live.lingting.protobuf.SecurityGrpcAuthorization;
import live.lingting.protobuf.SecurityGrpcAuthorizationServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-30 20:16
 */
class SecurityGrpcTest {

	SecurityGrpcExpandConvert convert;

	GrpcServer server;

	ManagedChannel channel;

	SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceBlockingStub blocking;

	@BeforeEach
	void before() {
		SecurityMemoryStore store = new SecurityMemoryStore();
		AuthorizationServiceImpl authorizationService = new AuthorizationServiceImpl(store);
		Password password = new Password();
		convert = new SecurityGrpcExpandConvert();

		SecurityGrpcAuthorizationEndpoint endpoint = new SecurityGrpcAuthorizationEndpoint(authorizationService, store,
				password, convert);
		GrpcServerProperties serverProperties = new GrpcServerProperties();
		SecurityGrpcProperties properties = new SecurityGrpcProperties();
		SecurityDefaultResourceServiceImpl resourceService = new SecurityDefaultResourceServiceImpl(store);
		SecurityAuthorize authorize = new SecurityAuthorize(0);
		server = new GrpcServerBuilder().port(0)
			.properties(serverProperties)
			.service(endpoint)
			.interceptor(new GrpcServerTraceIdInterceptor(serverProperties))
			.interceptor(new SecurityGrpcResourceServerInterceptor(properties.authorizationKey(), resourceService,
					authorize, new SecurityGrpcExceptionHandler()))
			.build();
		server.onApplicationStart();
		ValueUtils.awaitTrue(() -> server.isRunning());

		GrpcClientProperties clientProperties = new GrpcClientProperties();
		clientProperties.setUsePlaintext(true);
		List<ClientInterceptor> clientInterceptors = new ArrayList<>();
		clientInterceptors.add(new GrpcClientTraceIdInterceptor(clientProperties));
		clientInterceptors.add(new SecurityGrpcRemoteResourceClientInterceptor(properties));
		GrpcClientProvide provide = new GrpcClientProvide(clientProperties, clientInterceptors);

		channel = provide.channel("127.0.0.1", server.port());
		blocking = provide.blocking(channel, SecurityGrpcAuthorizationServiceGrpc::newBlockingStub);
	}

	@AfterEach
	void after() {
		server.onApplicationStop();
		channel.shutdownNow();
	}

	@Test
	void test() {
		// 登录
		SecurityGrpcAuthorization.AuthorizationVO grpcAdmin = blocking
			.password(SecurityGrpcAuthorization.AuthorizationPasswordPO.newBuilder()
				.setUsername("admin")
				.setPassword("")
				.build());
		ExpandAuthorizationVO admin = convert.voExpand(convert.toJava(grpcAdmin));
		assertEquals("admin", admin.getUserId());
		assertTrue(admin.isExpand());
		// 无token解析
		assertThrowsExactly(StatusRuntimeException.class, () -> blocking.resolve(Empty.getDefaultInstance()));
		// token解析
		SecurityTokenHolder.set(SecurityToken.ofDelimiter(admin.getToken(), " "));
		try {
			SecurityGrpcAuthorization.AuthorizationVO resolveGrpcAdmin = blocking.resolve(Empty.getDefaultInstance());
			ExpandAuthorizationVO resolveAdmin = convert.voExpand(convert.toJava(resolveGrpcAdmin));

			assertEquals(admin.getUsername(), resolveAdmin.getUsername());
			assertEquals(admin.getAttributes().get("tag"), resolveAdmin.getAttributes().get("tag"));

			SecurityGrpcAuthorization.AuthorizationVO logoutGrpcAdmin = blocking.logout(Empty.getDefaultInstance());
			ExpandAuthorizationVO logoutAdmin = convert.voExpand(convert.toJava(logoutGrpcAdmin));

			assertEquals(admin.getUsername(), logoutAdmin.getUsername());
			assertEquals(admin.getAttributes().get("tag"), logoutAdmin.getAttributes().get("tag"));

			// 退出登录解析
			assertThrowsExactly(StatusRuntimeException.class, () -> blocking.resolve(Empty.getDefaultInstance()));
		}
		finally {
			SecurityTokenHolder.remove();
		}
	}

}
