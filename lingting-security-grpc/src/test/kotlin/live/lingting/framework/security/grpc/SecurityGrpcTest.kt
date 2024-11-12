package live.lingting.framework.security.grpc

import com.google.protobuf.Empty
import io.grpc.Channel
import io.grpc.ClientInterceptor
import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import live.lingting.framework.endpoint.SecurityGrpcAuthorizationEndpoint
import live.lingting.framework.exception.SecurityGrpcExceptionInstance
import live.lingting.framework.grpc.GrpcClientProvide
import live.lingting.framework.grpc.GrpcServer
import live.lingting.framework.grpc.GrpcServerBuilder
import live.lingting.framework.grpc.exception.GrpcExceptionProcessor
import live.lingting.framework.grpc.interceptor.GrpcClientTraceIdInterceptor
import live.lingting.framework.grpc.interceptor.GrpcServerExceptionInterceptor
import live.lingting.framework.grpc.interceptor.GrpcServerTraceIdInterceptor
import live.lingting.framework.grpc.properties.GrpcClientProperties
import live.lingting.framework.grpc.properties.GrpcServerProperties
import live.lingting.framework.interceptor.SecurityGrpcRemoteContent.Companion.pop
import live.lingting.framework.interceptor.SecurityGrpcRemoteContent.Companion.put
import live.lingting.framework.interceptor.SecurityGrpcRemoteResourceClientInterceptor
import live.lingting.framework.interceptor.SecurityGrpcResourceServerInterceptor
import live.lingting.framework.properties.SecurityGrpcProperties
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceBlockingStub
import live.lingting.framework.security.authorize.SecurityAuthorize
import live.lingting.framework.security.domain.SecurityToken.Companion.ofDelimiter
import live.lingting.framework.security.grpc.authorization.AuthorizationServiceImpl
import live.lingting.framework.security.grpc.authorization.Password
import live.lingting.framework.security.resolver.SecurityTokenDefaultResolver
import live.lingting.framework.security.resolver.SecurityTokenResolver
import live.lingting.framework.security.resource.SecurityDefaultResourceServiceImpl
import live.lingting.framework.security.store.SecurityMemoryStore
import live.lingting.framework.util.ValueUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.List

/**
 * @author lingting 2024-01-30 20:16
 */
internal class SecurityGrpcTest {
    var convert: SecurityGrpcExpandConvert? = null

    var server: GrpcServer? = null

    var channel: ManagedChannel? = null

    var blocking: SecurityGrpcAuthorizationServiceBlockingStub? = null

    @BeforeEach
    fun before() {
        val store = SecurityMemoryStore()
        val authorizationService = AuthorizationServiceImpl(store)
        val password = Password()
        convert = SecurityGrpcExpandConvert()

        val endpoint = SecurityGrpcAuthorizationEndpoint(
            authorizationService, store,
            password, convert!!
        )
        val serverProperties = GrpcServerProperties()
        val properties = SecurityGrpcProperties()
        val resolvers: MutableList<SecurityTokenResolver> = ArrayList()
        resolvers.add(SecurityTokenDefaultResolver(store))
        val resourceService = SecurityDefaultResourceServiceImpl(resolvers)
        val authorize = SecurityAuthorize(0)
        val securityGrpcExceptionInstance = SecurityGrpcExceptionInstance()
        val processor = GrpcExceptionProcessor(List.of(securityGrpcExceptionInstance))
        server = GrpcServerBuilder().port(0)
            .properties(serverProperties)
            .service(endpoint)
            .interceptor(GrpcServerExceptionInterceptor(serverProperties, processor))
            .interceptor(GrpcServerTraceIdInterceptor(serverProperties))
            .interceptor(
                SecurityGrpcResourceServerInterceptor(
                    properties.authorizationKey(), resourceService,
                    authorize
                )
            )
            .build()
        server!!.onApplicationStart()
        ValueUtils.awaitTrue { server!!.isRunning }

        val clientProperties = GrpcClientProperties()
        clientProperties.isUsePlaintext = true
        val clientInterceptors: MutableList<ClientInterceptor?> = ArrayList()
        clientInterceptors.add(GrpcClientTraceIdInterceptor(clientProperties))
        clientInterceptors.add(SecurityGrpcRemoteResourceClientInterceptor(properties))
        val provide = GrpcClientProvide(clientProperties, clientInterceptors)

        channel = provide.channel("127.0.0.1", server!!.port())
        blocking = provide.blocking(channel) { channel: Channel? -> SecurityGrpcAuthorizationServiceGrpc.newBlockingStub(channel) }
    }

    @AfterEach
    fun after() {
        server!!.onApplicationStop()
        channel!!.shutdownNow()
    }

    @Test
    fun test() {
        // 登录
        val grpcAdmin = blocking
            .password(
                SecurityGrpcAuthorization.AuthorizationPasswordPO.newBuilder()
                    .setUsername("admin")
                    .setPassword("")
                    .build()
            )
        val admin = convert!!.voExpand(convert!!.toJava(grpcAdmin))
        Assertions.assertEquals("admin", admin.userId)
        Assertions.assertTrue(admin.isExpand)
        // 无token解析
        Assertions.assertThrowsExactly(StatusRuntimeException::class.java) { blocking!!.resolve(Empty.getDefaultInstance()) }
        // token解析
        put(ofDelimiter(admin.token!!, " "))
        try {
            val resolveGrpcAdmin = blocking!!.resolve(Empty.getDefaultInstance())
            val resolveAdmin = convert!!.voExpand(convert!!.toJava(resolveGrpcAdmin))

            Assertions.assertEquals(admin.username, resolveAdmin.username)
            Assertions.assertEquals(admin.attributes!!["tag"], resolveAdmin.attributes!!["tag"])

            val logoutGrpcAdmin = blocking!!.logout(Empty.getDefaultInstance())
            val logoutAdmin = convert!!.voExpand(convert!!.toJava(logoutGrpcAdmin))

            Assertions.assertEquals(admin.username, logoutAdmin.username)
            Assertions.assertEquals(admin.attributes!!["tag"], logoutAdmin.attributes!!["tag"])

            // 退出登录解析
            Assertions.assertThrowsExactly(StatusRuntimeException::class.java) { blocking!!.resolve(Empty.getDefaultInstance()) }
        } finally {
            pop()
        }
    }
}
