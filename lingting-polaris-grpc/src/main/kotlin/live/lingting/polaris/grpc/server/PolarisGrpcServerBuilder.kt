package live.lingting.polaris.grpc.server

import com.google.common.util.concurrent.MoreExecutors
import com.tencent.polaris.api.utils.StringUtils
import com.tencent.polaris.client.api.SDKContext
import io.grpc.BinaryLog
import io.grpc.BindableService
import io.grpc.CompressorRegistry
import io.grpc.DecompressorRegistry
import io.grpc.HandlerRegistry
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerStreamTracer
import io.grpc.ServerTransportFilter
import live.lingting.polaris.grpc.interceptor.PolarisServerInterceptor
import java.io.File
import java.io.InputStream
import java.time.Duration

import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisGrpcServerBuilder
/**
 * PolarisGrpcServerBuilder Constructor.
 * @param builder ServerBuilder
 */(private val builder: ServerBuilder<*>) : ServerBuilder<PolarisGrpcServerBuilder>() {
    private val polarisInterceptors: MutableList<PolarisServerInterceptor> = ArrayList()

    private val interceptors: MutableList<ServerInterceptor> = ArrayList()

    var applicationName: String? = null
        private set

    var namespace: String? = null
        private set

    var metaData: Map<String, String> = HashMap()
        private set

    var weight: Int = 100
        private set

    var version: String? = null
        private set

    var heartbeatInterval: Int = 0
        private set

    var host: String? = null
        private set

    var token: String? = null
        private set

    private var delayRegister: DelayRegister? = null

    var registerHook: RegisterHook? = null
        private set

    /**
     * gRPC-Server 优雅关闭最大等待时长
     */
    private var maxWaitDuration: Duration = Duration.ofSeconds(30)

    var context: SDKContext? = null
        private set

    /**
     * Set polaris SDK Context
     * @param context polaris sdk core object
     * @return PolarisGrpcServerBuilder
     */
    fun sdkContext(context: SDKContext?): PolarisGrpcServerBuilder {
        this.context = context
        return this
    }

    /**
     * Set grpc service name.
     * @param applicationName grpc server name
     * @return PolarisGrpcServerBuilder
     */
    fun applicationName(applicationName: String?): PolarisGrpcServerBuilder {
        this.applicationName = applicationName
        return this
    }

    /**
     * Namespace registered by grpc service.
     * @param namespace polaris namespace
     * @return PolarisGrpcServerBuilder
     */
    fun namespace(namespace: String?): PolarisGrpcServerBuilder {
        this.namespace = namespace
        return this
    }

    /**
     * Set metadata.
     * @param metadata metadata
     * @return PolarisGrpcServerBuilder
     */
    fun metadata(metadata: Map<String, String>): PolarisGrpcServerBuilder {
        this.metaData = metadata
        return this
    }

    /**
     * set instance weight
     * @param weight
     * @return PolarisGrpcServerBuilder
     */
    fun weight(weight: Int): PolarisGrpcServerBuilder {
        this.weight = weight
        return this
    }

    fun version(version: String?): PolarisGrpcServerBuilder {
        this.version = version
        return this
    }

    /**
     * Set the heartbeat report time by default 5 seconds.
     * @param heartbeatInterval Time in seconds
     * @return PolarisGrpcServerBuilder
     */
    fun heartbeatInterval(heartbeatInterval: Int): PolarisGrpcServerBuilder {
        this.heartbeatInterval = heartbeatInterval
        return this
    }

    /**
     * Set the local host.
     * @param host host
     * @return PolarisGrpcServerBuilder
     */
    fun host(host: String?): PolarisGrpcServerBuilder {
        this.host = host
        return this
    }

    /**
     * Set the token.
     * @param token token
     * @return PolarisGrpcServerBuilder
     */
    fun token(token: String?): PolarisGrpcServerBuilder {
        this.token = token
        return this
    }

    override fun directExecutor(): PolarisGrpcServerBuilder {
        return executor(MoreExecutors.directExecutor())
    }

    override fun executor(executor: Executor?): PolarisGrpcServerBuilder {
        builder.executor(executor)
        return this
    }

    override fun addService(service: ServerServiceDefinition): PolarisGrpcServerBuilder {
        builder.addService(service)
        return this
    }

    override fun addService(bindableService: BindableService): PolarisGrpcServerBuilder {
        builder.addService(bindableService)
        return this
    }

    override fun fallbackHandlerRegistry(fallbackRegistry: HandlerRegistry?): PolarisGrpcServerBuilder {
        builder.fallbackHandlerRegistry(fallbackRegistry)
        return this
    }

    override fun useTransportSecurity(certChain: File, privateKey: File): PolarisGrpcServerBuilder {
        builder.useTransportSecurity(certChain, privateKey)
        return this
    }

    override fun decompressorRegistry(registry: DecompressorRegistry?): PolarisGrpcServerBuilder {
        builder.decompressorRegistry(registry)
        return this
    }

    override fun compressorRegistry(registry: CompressorRegistry?): PolarisGrpcServerBuilder {
        builder.compressorRegistry(registry)
        return this
    }

    override fun intercept(interceptor: ServerInterceptor): PolarisGrpcServerBuilder {
        if (interceptor is PolarisServerInterceptor) {
            polarisInterceptors.add(interceptor)
        } else {
            interceptors.add(interceptor)
        }
        return this
    }

    override fun addTransportFilter(filter: ServerTransportFilter): PolarisGrpcServerBuilder {
        builder.addTransportFilter(filter)
        return this
    }

    override fun addStreamTracerFactory(factory: ServerStreamTracer.Factory): PolarisGrpcServerBuilder {
        builder.addStreamTracerFactory(factory)
        return this
    }

    override fun useTransportSecurity(certChain: InputStream, privateKey: InputStream): PolarisGrpcServerBuilder {
        builder.useTransportSecurity(certChain, privateKey)
        return this
    }

    override fun handshakeTimeout(timeout: Long, unit: TimeUnit): PolarisGrpcServerBuilder {
        builder.handshakeTimeout(timeout, unit)
        return this
    }

    override fun keepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit): PolarisGrpcServerBuilder {
        builder.keepAliveTime(keepAliveTime, timeUnit)
        return this
    }

    override fun keepAliveTimeout(keepAliveTimeout: Long, timeUnit: TimeUnit): PolarisGrpcServerBuilder {
        builder.keepAliveTimeout(keepAliveTimeout, timeUnit)
        return this
    }

    override fun maxConnectionIdle(maxConnectionIdle: Long, timeUnit: TimeUnit): PolarisGrpcServerBuilder {
        builder.maxConnectionIdle(maxConnectionIdle, timeUnit)
        return this
    }

    override fun maxConnectionAge(maxConnectionAge: Long, timeUnit: TimeUnit): PolarisGrpcServerBuilder {
        builder.maxConnectionAge(maxConnectionAge, timeUnit)
        return this
    }

    override fun maxConnectionAgeGrace(maxConnectionAgeGrace: Long, timeUnit: TimeUnit): PolarisGrpcServerBuilder {
        builder.maxConnectionAgeGrace(maxConnectionAgeGrace, timeUnit)
        return this
    }

    override fun permitKeepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit): PolarisGrpcServerBuilder {
        builder.permitKeepAliveTime(keepAliveTime, timeUnit)
        return this
    }

    override fun permitKeepAliveWithoutCalls(permit: Boolean): PolarisGrpcServerBuilder {
        builder.permitKeepAliveWithoutCalls(permit)
        return this
    }

    override fun maxInboundMessageSize(bytes: Int): PolarisGrpcServerBuilder {
        builder.maxInboundMessageSize(bytes)
        return this
    }

    override fun maxInboundMetadataSize(bytes: Int): PolarisGrpcServerBuilder {
        builder.maxInboundMetadataSize(bytes)
        return this
    }

    override fun setBinaryLog(binaryLog: BinaryLog): PolarisGrpcServerBuilder {
        builder.setBinaryLog(binaryLog)
        return this
    }

    /**
     * 延迟注册, 用户可以通过设置 [DelayRegister] 来延迟 gRPC-server 注册到 polaris 对外提供服务的时间 默认支持策略 -
     * [live.lingting.polaris.grpc.server.impl.WaitDelayRegister] 等待一段时间在进行注册
     * @param delayRegister [DelayRegister]
     * @return [PolarisGrpcServerBuilder]
     */
    fun delayRegister(delayRegister: DelayRegister?): PolarisGrpcServerBuilder {
        this.delayRegister = delayRegister
        return this
    }

    /**
     * 优雅下线的最大等待时间，如果到了一定时间还没有结束，则直接强制关闭，默认 Duration.ofSeconds(30)
     * @param maxWaitDuration [Duration]
     * @return [PolarisGrpcServerBuilder]
     */
    fun maxWaitDuration(maxWaitDuration: Duration): PolarisGrpcServerBuilder {
        this.maxWaitDuration = maxWaitDuration
        return this
    }

    fun registerHook(registerHook: RegisterHook?): PolarisGrpcServerBuilder {
        this.registerHook = registerHook
        return this
    }

    override fun build(): Server {
        setDefault()
        for (interceptor in polarisInterceptors) {
            interceptor.init(namespace, applicationName, context!!)
            builder.intercept(interceptor)
        }
        for (interceptor in interceptors) {
            builder.intercept(interceptor)
        }

        val server = PolarisGrpcServer(this, context, builder.build())
        server.setDelayRegister(delayRegister)
        server.setMaxWaitDuration(maxWaitDuration)

        return server
    }

    private fun setDefault() {
        if (Objects.isNull(context)) {
            context = SDKContext.initContext()
        }
        if (StringUtils.isBlank(namespace)) {
            this.namespace = DEFAULT_NAMESPACE
        }
        if (heartbeatInterval == 0) {
            this.heartbeatInterval = DEFAULT_TTL
        }
    }

    companion object {
        private const val DEFAULT_NAMESPACE = "default"

        private const val DEFAULT_TTL = 5

        /**
         * Static factory for creating a new PolarisGrpcServerBuilder.
         * @param port the port to listen on
         * @return PolarisGrpcServerBuilder
         */
        fun forPort(port: Int): PolarisGrpcServerBuilder {
            val builder = ServerBuilder.forPort(port)
            return PolarisGrpcServerBuilder(builder)
        }
    }
}
