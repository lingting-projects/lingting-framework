package live.lingting.polaris.grpc.client

import com.tencent.polaris.api.pojo.ServiceKey
import com.tencent.polaris.client.api.SDKContext
import io.grpc.BinaryLog
import io.grpc.ClientInterceptor
import io.grpc.CompressorRegistry
import io.grpc.DecompressorRegistry
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.NameResolver
import io.grpc.ProxyDetector
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.polaris.grpc.interceptor.PolarisClientInterceptor
import live.lingting.polaris.grpc.loadbalance.PolarisLoadBalancerFactory
import live.lingting.polaris.grpc.loadbalance.PolarisLoadBalancerProvider
import live.lingting.polaris.grpc.resolver.PolarisNameResolverFactory
import live.lingting.polaris.grpc.util.JvmHookHelper

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisManagedChannelBuilder private constructor(
    sourceService: ServiceKey?, sdkContext: SDKContext?,
    builder: ManagedChannelBuilder<*>
) : ManagedChannelBuilder<PolarisManagedChannelBuilder>() {
    private val builder: ManagedChannelBuilder<*>

    private val polarisInterceptors: MutableList<PolarisClientInterceptor> = ArrayList()

    private val interceptors: MutableList<ClientInterceptor> = ArrayList()

    private val sourceService: ServiceKey?

    init {
        var sdkContext = sdkContext
        if (FIRST_INIT.compareAndSet(false, true)) {
            if (sdkContext == null) {
                sdkContext = SDKContext.initContext()
                JvmHookHelper.addShutdownHook(Runnable { sdkContext.destroy() })
            }
            PolarisLoadBalancerFactory.init(sdkContext)
            PolarisNameResolverFactory.init(sdkContext)
            sDKContext = sdkContext
            synchronized(MONITOR) {
                (MONITOR as Object).notifyAll()
            }
        }
        var prev = sDKContext
        if (prev == null) {
            synchronized(MONITOR) {
                try {
                    (MONITOR as Object).wait()
                } catch (e: InterruptedException) {
                    // noop
                }
                prev = sDKContext
            }
        }
        check(!(sdkContext != null && prev !== sdkContext)) { "[Polaris] SDKContext already initialize" }

        this.builder = builder
        this.sourceService = sourceService
    }

    override fun directExecutor(): PolarisManagedChannelBuilder {
        builder.directExecutor()
        return this
    }

    override fun executor(executor: Executor): PolarisManagedChannelBuilder {
        builder.executor(executor)
        return this
    }

    override fun intercept(interceptors: List<ClientInterceptor>): PolarisManagedChannelBuilder {
        for (interceptor in interceptors) {
            if (interceptor is PolarisClientInterceptor) {
                polarisInterceptors.add(interceptor)
            } else {
                this.interceptors.add(interceptor)
            }
        }
        return this
    }

    override fun intercept(vararg interceptors: ClientInterceptor): PolarisManagedChannelBuilder {
        for (interceptor in interceptors) {
            if (interceptor is PolarisClientInterceptor) {
                polarisInterceptors.add(interceptor)
            } else {
                this.interceptors.add(interceptor)
            }
        }
        return this
    }

    override fun userAgent(userAgent: String): PolarisManagedChannelBuilder {
        builder.userAgent(userAgent)
        return this
    }

    override fun overrideAuthority(authority: String): PolarisManagedChannelBuilder {
        builder.overrideAuthority(authority)
        return this
    }

    override fun nameResolverFactory(resolverFactory: NameResolver.Factory): PolarisManagedChannelBuilder {
        builder.nameResolverFactory(resolverFactory)
        return this
    }

    override fun decompressorRegistry(registry: DecompressorRegistry): PolarisManagedChannelBuilder {
        builder.decompressorRegistry(registry)
        return this
    }

    override fun compressorRegistry(registry: CompressorRegistry): PolarisManagedChannelBuilder {
        builder.compressorRegistry(registry)
        return this
    }

    override fun idleTimeout(value: Long, unit: TimeUnit): PolarisManagedChannelBuilder {
        builder.idleTimeout(value, unit)
        return this
    }

    override fun offloadExecutor(executor: Executor): PolarisManagedChannelBuilder {
        builder.offloadExecutor(executor)
        return this
    }

    override fun usePlaintext(): PolarisManagedChannelBuilder {
        builder.usePlaintext()
        return this
    }

    override fun useTransportSecurity(): PolarisManagedChannelBuilder {
        builder.useTransportSecurity()
        return this
    }

    override fun maxInboundMessageSize(bytes: Int): PolarisManagedChannelBuilder {
        builder.maxInboundMessageSize(bytes)
        return this
    }

    override fun maxInboundMetadataSize(bytes: Int): PolarisManagedChannelBuilder {
        builder.maxInboundMetadataSize(bytes)
        return this
    }

    override fun keepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit): PolarisManagedChannelBuilder {
        builder.keepAliveTime(keepAliveTime, timeUnit)
        return this
    }

    override fun keepAliveTimeout(keepAliveTimeout: Long, timeUnit: TimeUnit): PolarisManagedChannelBuilder {
        builder.keepAliveTimeout(keepAliveTimeout, timeUnit)
        return this
    }

    override fun keepAliveWithoutCalls(enable: Boolean): PolarisManagedChannelBuilder {
        builder.keepAliveWithoutCalls(enable)
        return this
    }

    override fun maxRetryAttempts(maxRetryAttempts: Int): PolarisManagedChannelBuilder {
        builder.maxRetryAttempts(maxRetryAttempts)
        return this
    }

    override fun maxHedgedAttempts(maxHedgedAttempts: Int): PolarisManagedChannelBuilder {
        builder.maxHedgedAttempts(maxHedgedAttempts)
        return this
    }

    override fun retryBufferSize(bytes: Long): PolarisManagedChannelBuilder {
        builder.retryBufferSize(bytes)
        return this
    }

    override fun perRpcBufferLimit(bytes: Long): PolarisManagedChannelBuilder {
        builder.perRpcBufferLimit(bytes)
        return this
    }

    override fun disableRetry(): PolarisManagedChannelBuilder {
        builder.disableRetry()
        return this
    }

    override fun enableRetry(): PolarisManagedChannelBuilder {
        builder.enableRetry()
        return this
    }

    override fun setBinaryLog(binaryLog: BinaryLog): PolarisManagedChannelBuilder {
        builder.setBinaryLog(binaryLog)
        return this
    }

    override fun maxTraceEvents(maxTraceEvents: Int): PolarisManagedChannelBuilder {
        builder.maxTraceEvents(maxTraceEvents)
        return this
    }

    override fun proxyDetector(proxyDetector: ProxyDetector): PolarisManagedChannelBuilder {
        builder.proxyDetector(proxyDetector)
        return this
    }

    override fun defaultServiceConfig(serviceConfig: Map<String?, *>?): PolarisManagedChannelBuilder {
        builder.defaultServiceConfig(serviceConfig)
        return this
    }

    override fun disableServiceConfigLookUp(): PolarisManagedChannelBuilder {
        builder.disableServiceConfigLookUp()
        return this
    }

    override fun build(): ManagedChannel {
        for (clientInterceptor in polarisInterceptors) {
            clientInterceptor.init(sourceService!!.namespace, sourceService.service, sDKContext)
            builder.intercept(clientInterceptor)
        }
        builder.intercept(interceptors)
        builder.defaultLoadBalancingPolicy(PolarisLoadBalancerProvider.LOADBALANCER_PROVIDER)
        return builder.build()
    }

    companion object {
        private val FIRST_INIT = AtomicBoolean(false)

        private val MONITOR = Any()

        @Volatile
        var sDKContext: SDKContext? = null
            private set

        /**
         * 增强 [ManagedChannelBuilder.forTarget], 在连接到目标服务时允许设置主调服务的相关信息,
         * 并且可以自定义北极星 SDK 的核心数据结构 [SDKContext]
         * @param target 服务名
         * @param sourceService [ServiceKey] 主调服务信息以及标签
         * @param sdkContext [SDKContext] 可以设置北极星 SDK 的相关配置以及行为, 例如服务治理中心地址等等
         * @return [PolarisManagedChannelBuilder]
         */
        /**
         * follow [ManagedChannelBuilder.forTarget]
         * @param target 服务名
         * @return [PolarisManagedChannelBuilder]
         */
        /**
         * 增强 [ManagedChannelBuilder.forTarget], 在连接到目标服务时允许设置主调服务的相关信息
         * @param target 服务名
         * @param sourceService [ServiceKey] 主调服务信息以及标签
         * @return [PolarisManagedChannelBuilder]
         */
        @JvmOverloads
        fun forTarget(
            target: String, sourceService: ServiceKey? = null,
            sdkContext: SDKContext? = null
        ): PolarisManagedChannelBuilder {
            val forTarget = ManagedChannelBuilder.forTarget(buildUrl(target, sourceService))
            return forTarget(sourceService, sdkContext, forTarget)
        }

        fun forTarget(
            sourceService: ServiceKey?, sdkContext: SDKContext?,
            builder: ManagedChannelBuilder<*>
        ): PolarisManagedChannelBuilder {
            return PolarisManagedChannelBuilder(sourceService, sdkContext, builder)
        }

        fun resetSDKContext() {
            sDKContext = null
        }

        fun buildUrl(target: String, sourceService: ServiceKey?): String {
            var target = target
            if (Objects.isNull(sourceService)) {
                return target
            }

            val json = JacksonUtils.toJson(sourceService)
            val extendInfo = Base64.getUrlEncoder().encodeToString(json.toByteArray(StandardCharsets.UTF_8))

            target += if (target.contains("?")) {
                "&extend_info=$extendInfo"
            } else {
                "?extend_info=$extendInfo"
            }

            return target
        }
    }
}
