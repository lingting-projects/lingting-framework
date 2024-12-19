package live.lingting.framework.security.grpc

import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import live.lingting.framework.grpc.GrpcClientProvide
import live.lingting.framework.grpc.GrpcServer
import live.lingting.framework.grpc.GrpcServerBuilder
import live.lingting.framework.grpc.customizer.ClientCustomizer
import live.lingting.framework.grpc.customizer.ServerCustomizer
import live.lingting.framework.grpc.customizer.ThreadExecutorCustomizer
import live.lingting.framework.grpc.exception.GrpcExceptionProcessor
import live.lingting.framework.grpc.interceptor.GrpcClientTraceIdInterceptor
import live.lingting.framework.grpc.interceptor.GrpcServerExceptionInterceptor
import live.lingting.framework.grpc.interceptor.GrpcServerTraceIdInterceptor
import live.lingting.framework.grpc.properties.GrpcClientProperties
import live.lingting.framework.grpc.properties.GrpcServerProperties
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc
import live.lingting.framework.protobuf.SecurityGrpcAuthorizationServiceGrpc.SecurityGrpcAuthorizationServiceBlockingStub
import live.lingting.framework.security.SecurityEndpointService
import live.lingting.framework.security.authorize.SecurityAuthorize
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.grpc.authorization.AuthorizationServiceImpl
import live.lingting.framework.security.grpc.authorization.Password
import live.lingting.framework.security.grpc.endpoint.SecurityGrpcAuthorizationEndpoint
import live.lingting.framework.security.grpc.exception.SecurityGrpcExceptionInstance
import live.lingting.framework.security.grpc.interceptor.GzipInterceptor
import live.lingting.framework.security.grpc.interceptor.SecurityGrpcRemoteContent.pop
import live.lingting.framework.security.grpc.interceptor.SecurityGrpcRemoteContent.put
import live.lingting.framework.security.grpc.interceptor.SecurityGrpcRemoteResourceClientInterceptor
import live.lingting.framework.security.grpc.interceptor.SecurityGrpcResourceServerInterceptor
import live.lingting.framework.security.grpc.properties.SecurityGrpcProperties
import live.lingting.framework.security.resolver.SecurityTokenDefaultResolver
import live.lingting.framework.security.resolver.SecurityTokenResolver
import live.lingting.framework.security.resource.SecurityDefaultResourceServiceImpl
import live.lingting.framework.security.store.SecurityMemoryStore
import live.lingting.framework.util.ValueUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

        val service = SecurityEndpointService(
            authorizationService, store,
            password, convert!!
        )

        val clientCustomizers = mutableListOf<ClientCustomizer>()
        val serverCustomizers = mutableListOf<ServerCustomizer>()

        val threadExecutorCustomizer = ThreadExecutorCustomizer()
        clientCustomizers.add(threadExecutorCustomizer)
        serverCustomizers.add(threadExecutorCustomizer)

        val gzipInterceptor = GzipInterceptor()
        val endpoint = SecurityGrpcAuthorizationEndpoint(service, convert!!)
        val serverProperties = GrpcServerProperties()
        serverProperties.useGzip = true

        val clientProperties = GrpcClientProperties()
        clientProperties.usePlaintext = true
        clientProperties.useGzip = true

        val properties = SecurityGrpcProperties()
        val resolvers: MutableList<SecurityTokenResolver> = ArrayList()
        resolvers.add(SecurityTokenDefaultResolver(store))
        val resourceService = SecurityDefaultResourceServiceImpl(resolvers)
        val authorize = SecurityAuthorize(0)
        val securityGrpcExceptionInstance = SecurityGrpcExceptionInstance()
        val processor = GrpcExceptionProcessor(listOf(securityGrpcExceptionInstance))
        val authorizationKey = properties.authorizationKey()
        server = GrpcServerBuilder().port(0)
            .properties(serverProperties)
            .service(endpoint)
            .interceptor(gzipInterceptor)
            .interceptor(GrpcServerExceptionInterceptor(serverProperties, processor))
            .interceptor(GrpcServerTraceIdInterceptor(serverProperties))
            .interceptor(SecurityGrpcResourceServerInterceptor(authorizationKey, resourceService, authorize, convert!!))
            .build(customizers = serverCustomizers)
        server!!.onApplicationStart()
        ValueUtils.awaitTrue { server!!.isRunning }

        val clientInterceptors = listOf(
            gzipInterceptor,
            GrpcClientTraceIdInterceptor(clientProperties),
            SecurityGrpcRemoteResourceClientInterceptor(properties),
        )
        val provide = GrpcClientProvide(clientProperties, clientInterceptors, customizers = clientCustomizers)

        channel = provide.channel("127.0.0.1", server!!.port())
        blocking = provide.blocking(channel!!) { channel -> SecurityGrpcAuthorizationServiceGrpc.newBlockingStub(channel) }
    }

    @AfterEach
    fun after() {
        server?.onApplicationStop()
        channel?.shutdownNow()
    }

    @Test
    fun test() {
        // 登录
        val grpcAdmin = blocking!!.password(
            SecurityGrpcAuthorization.AuthorizationPasswordPO.newBuilder()
                .setUsername("admin")
                .setPassword("")
                .build()
        )
        val admin = convert!!.voExpand(convert!!.toJava(grpcAdmin))
        assertEquals("admin", admin.userId)
        assertTrue(admin.isExpand)
        // 无token解析
        assertThrowsExactly(StatusRuntimeException::class.java) { blocking!!.resolve(SecurityGrpcAuthorization.TokenPO.newBuilder().buildPartial()) }
        // token解析
        put(SecurityToken.ofDelimiter(admin.authorization, " "))
        try {
            val resolveGrpcAdmin = blocking!!.resolve(
                SecurityGrpcAuthorization.TokenPO.newBuilder()
                    .setValue(admin.authorization)
                    .buildPartial()
            )
            val resolveAdmin = convert!!.voExpand(convert!!.toJava(resolveGrpcAdmin))

            assertEquals(admin.username, resolveAdmin.username)
            assertEquals(admin.attributes["tag"], resolveAdmin.attributes["tag"])

            val logoutGrpcAdmin = blocking!!.logout(Empty.getDefaultInstance())
            val logoutAdmin = convert!!.voExpand(convert!!.toJava(logoutGrpcAdmin))

            assertEquals(admin.username, logoutAdmin.username)
            assertEquals(admin.attributes["tag"], logoutAdmin.attributes["tag"])

            // 退出登录解析
            assertThrowsExactly(StatusRuntimeException::class.java) {
                blocking!!.resolve(
                    SecurityGrpcAuthorization.TokenPO.newBuilder()
                        .setValue(admin.authorization)
                        .buildPartial()
                )
            }
        } finally {
            pop()
        }
    }
}
