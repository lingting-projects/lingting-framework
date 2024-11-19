package live.lingting.polaris.grpc.server

import com.tencent.polaris.api.core.ProviderAPI
import com.tencent.polaris.api.rpc.InstanceDeregisterRequest
import com.tencent.polaris.api.rpc.InstanceRegisterRequest
import com.tencent.polaris.api.utils.StringUtils
import com.tencent.polaris.client.api.SDKContext
import com.tencent.polaris.factory.api.DiscoveryAPIFactory
import io.grpc.Server
import io.grpc.ServerServiceDefinition
import java.net.SocketAddress
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import live.lingting.polaris.grpc.server.impl.NoopDelayRegister
import live.lingting.polaris.grpc.util.NetworkHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lixiaoshuang
 */
class PolarisGrpcServer internal constructor(private val builder: PolarisGrpcServerBuilder, private val context: SDKContext?, private var targetServer: Server) : Server() {
    private val providerAPI: ProviderAPI = DiscoveryAPIFactory.createProviderAPIByContext(context)

    private val shutdownOnce = AtomicBoolean(false)

    private val executorService: ScheduledExecutorService = ScheduledThreadPoolExecutor(2) { r: Runnable? ->
        val t = Thread(r)
        t.isDaemon = true
        t.name = "polaris-grpc-server"
        t
    }

    private var host: String? = null

    private var delayRegister: DelayRegister = NoopDelayRegister()

    private var maxWaitDuration: Duration? = null

    private val registerHook: RegisterHook? = builder.registerHook


    override fun start(): Server {
        initLocalHost()
        targetServer = targetServer.start()

        if (Objects.nonNull(delayRegister)) {
            executorService.execute {
                while (true) {
                    if (delayRegister.allowRegis()) {
                        break
                    }
                }
                this.registerInstance(targetServer.services)
            }
        }

        return this
    }

    override fun getPort(): Int {
        return targetServer.port
    }

    override fun getListenSockets(): List<SocketAddress> {
        return targetServer.listenSockets
    }

    override fun getServices(): List<ServerServiceDefinition> {
        return targetServer.services
    }

    override fun getImmutableServices(): List<ServerServiceDefinition> {
        return targetServer.immutableServices
    }

    override fun getMutableServices(): List<ServerServiceDefinition> {
        return targetServer.mutableServices
    }

    override fun shutdown(): Server {
        if (shutdownOnce.compareAndSet(false, true)) {
            executorService.shutdownNow()
            // 将自己从注册中心反注册掉
            this.deregister(targetServer.services)
            providerAPI.destroy()
        }

        return GraceOffline(targetServer, maxWaitDuration, context).shutdown()
    }

    override fun shutdownNow(): Server {
        if (shutdownOnce.compareAndSet(false, true)) {
            executorService.shutdownNow()
            this.deregister(targetServer.services)
            providerAPI.destroy()
            context!!.close()
        }
        return targetServer.shutdownNow()
    }

    override fun isShutdown(): Boolean {
        return targetServer.isShutdown
    }

    override fun isTerminated(): Boolean {
        return targetServer.isTerminated
    }


    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return targetServer.awaitTermination(timeout, unit)
    }


    override fun awaitTermination() {
        targetServer.awaitTermination()
    }

    fun setDelayRegister(delayRegister: DelayRegister?) {
        if (delayRegister == null) {
            return
        }
        this.delayRegister = delayRegister
    }

    private fun initLocalHost() {
        host = builder.host
        if (StringUtils.isNotBlank(host)) {
            return
        }
        val polarisServerAddr = context!!.config.global.serverConnector.addresses[0]
        val detail = polarisServerAddr.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        host = NetworkHelper.Companion.getLocalHost(detail[0], detail[1].toInt())
    }

    /**
     * This interface will determine whether it is an interface-level registration
     * instance or an application-level instance registration based on
     * grpcServiceRegister.
     */
    private fun registerInstance(definitions: List<ServerServiceDefinition>) {
        if (StringUtils.isNotBlank(builder.applicationName)) {
            this.registerOne(builder.applicationName)
            return
        }
        for (definition in definitions) {
            val grpcServiceName = definition.serviceDescriptor.name
            this.registerOne(grpcServiceName)
        }
    }

    /**
     * Register a service instance.
     *
     * @param serviceName service name
     */
    private fun registerOne(serviceName: String?) {
        val request = InstanceRegisterRequest()
        request.namespace = builder.namespace
        request.service = serviceName
        request.host = host
        request.token = builder.token
        request.version = builder.version
        request.protocol = "grpc"
        request.weight = builder.weight
        request.port = targetServer.port
        request.ttl = builder.heartbeatInterval
        request.metadata = builder.metaData

        if (Objects.nonNull(registerHook)) {
            registerHook!!.beforeRegister(request)
        }

        val response = providerAPI.registerInstance(request)

        if (Objects.nonNull(registerHook)) {
            registerHook!!.afterRegister(response)
        }

        LOG.info("[grpc-polaris] register polaris success, instance-id:{}", response.instanceId)
    }

    /**
     * Service deregister.
     *
     * @param definitions Definition of a service
     */
    private fun deregister(definitions: List<ServerServiceDefinition>) {
        LOG.info("[grpc-polaris] begin do deregister grpc service")
        if (StringUtils.isNotBlank(builder.applicationName)) {
            this.deregisterOne(builder.applicationName)
            return
        }
        for (definition in definitions) {
            val grpcServiceName = definition.serviceDescriptor.name
            this.deregisterOne(grpcServiceName)
        }
    }

    /**
     * deregister a service instance.
     *
     * @param serviceName service name
     */
    private fun deregisterOne(serviceName: String?) {
        val request = InstanceDeregisterRequest()
        request.namespace = builder.namespace
        request.service = serviceName
        request.host = host
        request.port = targetServer.port
        providerAPI.deRegister(request)
    }

    fun setMaxWaitDuration(maxWaitDuration: Duration?) {
        this.maxWaitDuration = maxWaitDuration
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(PolarisGrpcServer::class.java)
    }
}
