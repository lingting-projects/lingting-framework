package live.lingting.polaris.grpc.resolver

import com.tencent.polaris.api.core.ConsumerAPI
import com.tencent.polaris.api.listener.ServiceListener
import com.tencent.polaris.api.pojo.Instance
import com.tencent.polaris.api.pojo.ServiceChangeEvent
import com.tencent.polaris.api.pojo.ServiceKey
import com.tencent.polaris.api.rpc.GetHealthyInstancesRequest
import com.tencent.polaris.api.rpc.InstancesResponse
import com.tencent.polaris.api.rpc.UnWatchServiceRequest
import com.tencent.polaris.api.rpc.WatchServiceRequest
import com.tencent.polaris.client.api.SDKContext
import io.grpc.Attributes
import io.grpc.EquivalentAddressGroup
import io.grpc.NameResolver
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.polaris.grpc.util.Common
import live.lingting.polaris.grpc.util.NetworkHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.URI
import java.util.*
import java.util.function.Consumer

/**
 * Service discovery class
 *
 * @author lixiaoshuang
 */
class PolarisNameResolver(private val targetUri: URI, context: SDKContext, consumerAPI: ConsumerAPI) : NameResolver() {
    private val consumerAPI: ConsumerAPI

    private val namespace: String

    private val service: String

    private val context: SDKContext

    private val interceptors: MutableList<ResolverInterceptor> = ArrayList()

    private var watcher: ServiceChangeWatcher? = null

    private var sourceService: ServiceKey? = null

    init {
        val params: Map<String, String?> = NetworkHelper.Companion.getUrlParams(targetUri.query)
        this.service = targetUri.host
        this.namespace = if (params["namespace"] == null) DEFAULT_NAMESPACE else params["namespace"]!!
        this.context = context
        this.consumerAPI = consumerAPI
        ServiceLoader.load(ResolverInterceptor::class.java).iterator().forEachRemaining { e: ResolverInterceptor -> interceptors.add(e) }
        interceptors.sort(Comparator.comparingInt { obj: ResolverInterceptor -> obj.priority() })
        if (params.containsKey("extend_info")) {
            val json = String(Base64.getUrlDecoder().decode(params["extend_info"]))
            this.sourceService = JacksonUtils.toObj(json, ServiceKey::class.java)
        }
    }

    override fun getServiceAuthority(): String {
        return service
    }

    override fun start(listener: Listener2) {
        doResolve(listener)
        doWatch(listener)
    }

    private fun doResolve(listener: Listener2) {
        val resolverContext: ResolverContext = ResolverContext.Companion.builder()
            .context(context)
            .targetUri(targetUri)
            .sourceService(sourceService)
            .build()
        interceptors.forEach(Consumer { resolverInterceptor: ResolverInterceptor -> resolverInterceptor.before(resolverContext) })

        val request = GetHealthyInstancesRequest()
        request.namespace = namespace
        request.service = service
        var response = consumerAPI.getHealthyInstances(request)
        LOG.info(
            "[grpc-polaris] namespace:{} service:{} instance size:{}", namespace, service,
            response!!.instances.size
        )

        for (interceptor in interceptors) {
            response = interceptor.after(resolverContext, response)
        }

        LOG.info(
            "[grpc-polaris] after namespace:{} service:{} instance size:{}", namespace, service,
            response!!.instances.size
        )
        notifyListener(listener, response)
    }

    private fun doWatch(listener: Listener2) {
        this.watcher = ServiceChangeWatcher(listener)
        consumerAPI.watchService(
            WatchServiceRequest.builder()
                .namespace(namespace)
                .service(service)
                .listeners(listOf<ServiceListener?>(this.watcher))
                .build()
        )
    }

    private fun notifyListener(listener: Listener2, response: InstancesResponse) {
        val serviceInstances = response.toServiceInstances()
        val equivalentAddressGroups: MutableList<EquivalentAddressGroup> = ArrayList()
        for (instance in serviceInstances.instances) {
            if ("grpc" == instance.protocol) {
                equivalentAddressGroups.add(buildEquivalentAddressGroup(instance))
            }
        }

        val builder = Attributes.newBuilder()

        if (sourceService != null) {
            builder.set<ServiceKey>(Common.Companion.SOURCE_SERVICE_INFO, sourceService)
        }

        listener.onResult(
            ResolutionResult.newBuilder()
                .setAddresses(equivalentAddressGroups)
                .setAttributes(builder.build())
                .build()
        )
    }

    override fun shutdown() {
        if (this.watcher != null) {
            consumerAPI.unWatchService(
                UnWatchServiceRequest.UnWatchServiceRequestBuilder.anUnWatchServiceRequest()
                    .listeners(listOf<ServiceListener?>(this.watcher))
                    .namespace(namespace)
                    .service(service)
                    .build()
            )
        }
    }

    private fun buildEquivalentAddressGroup(instance: Instance): EquivalentAddressGroup {
        val address = InetSocketAddress(instance.host, instance.port)
        val attributes = Attributes.newBuilder()
            .set<Instance>(Common.Companion.INSTANCE_KEY, instance)
            .set<String>(Common.Companion.TARGET_NAMESPACE_KEY, namespace)
            .set<String>(Common.Companion.TARGET_SERVICE_KEY, service)
            .build()
        return EquivalentAddressGroup(address, attributes)
    }

    private inner class ServiceChangeWatcher(private val listener: Listener2) : ServiceListener {
        override fun onEvent(event: ServiceChangeEvent) {
            doResolve(listener)
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(PolarisNameResolver::class.java)

        private const val DEFAULT_NAMESPACE = "default"
    }
}
