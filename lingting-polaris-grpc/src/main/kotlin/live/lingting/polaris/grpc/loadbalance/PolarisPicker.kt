package live.lingting.polaris.grpc.loadbalance

import com.google.common.base.Preconditions
import com.tencent.polaris.api.core.ConsumerAPI
import com.tencent.polaris.api.exception.PolarisException
import com.tencent.polaris.api.pojo.DefaultServiceInstances
import com.tencent.polaris.api.pojo.Instance
import com.tencent.polaris.api.pojo.RouteArgument
import com.tencent.polaris.api.pojo.ServiceEventKey
import com.tencent.polaris.api.pojo.ServiceInstances
import com.tencent.polaris.api.pojo.ServiceKey
import com.tencent.polaris.api.pojo.SourceService
import com.tencent.polaris.api.rpc.GetServiceRuleRequest
import com.tencent.polaris.api.utils.RuleUtils
import com.tencent.polaris.api.utils.StringUtils
import com.tencent.polaris.client.api.SDKContext
import com.tencent.polaris.router.api.core.RouterAPI
import com.tencent.polaris.router.api.rpc.ProcessLoadBalanceRequest
import com.tencent.polaris.router.api.rpc.ProcessRoutersRequest
import com.tencent.polaris.specification.api.v1.traffic.manage.RoutingProto
import com.tencent.polaris.specification.api.v1.traffic.manage.RoutingProto.Routing
import io.grpc.Attributes
import io.grpc.LoadBalancer
import io.grpc.LoadBalancer.PickResult
import io.grpc.LoadBalancer.PickSubchannelArgs
import io.grpc.LoadBalancer.SubchannelPicker
import io.grpc.Metadata
import io.grpc.Status
import java.util.function.Consumer
import live.lingting.polaris.grpc.util.ClientCallInfo
import live.lingting.polaris.grpc.util.Common
import live.lingting.polaris.grpc.util.PolarisHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The main balancing logic. It **must be thread-safe**. Typically it should
 * only synchronize on its own state, and avoid synchronizing with the LoadBalancer's
 * state.
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisPicker(
    private val channels: Map<PolarisSubChannel?, PolarisSubChannel?>, private val context: SDKContext,
    private val consumerAPI: ConsumerAPI, private val routerAPI: RouterAPI, private val sourceService: ServiceKey?,
    private val attributes: Attributes?
) : SubchannelPicker() {
    override fun pickSubchannel(args: PickSubchannelArgs): PickResult {
        if (channels.isEmpty()) {
            return PickResult.withNoResult()
        }

        val targetNamespace = attributes!!.get<String>(Common.TARGET_NAMESPACE_KEY)
        val targetService = attributes.get<String>(Common.TARGET_SERVICE_KEY)
        val target = ServiceKey(targetNamespace, targetService)

        val instances: MutableList<Instance> = ArrayList()
        channels.forEach { (key: PolarisSubChannel?, `val`: PolarisSubChannel?) -> instances.add(`val`!!) }

        val serviceInstances: ServiceInstances = DefaultServiceInstances(target, instances)

        try {
            val instance = doLoadBalance(doRoute(serviceInstances, target, args))
            val channel: LoadBalancer.Subchannel? = channels[PolarisSubChannel(instance)]

            if (Objects.isNull(channel)) {
                return PickResult.withNoResult()
            }

            return PickResult.withSubchannel(
                channel,
                PolarisClientStreamTracerFactory(
                    ClientCallInfo.builder()
                        .consumerAPI(consumerAPI)
                        .instance(instance)
                        .targetNamespace(targetNamespace)
                        .targetService(targetService)
                        .method(args.methodDescriptor.bareMethodName)
                        .build()
                )
            )
        } catch (e: PolarisException) {
            LOG.error("[grpc-polaris] pick subChannel fail", e)
            return PickResult.withError(Status.UNKNOWN.withCause(e))
        }
    }

    fun doLoadBalance(serviceInstances: ServiceInstances): Instance {
        if (serviceInstances.instances.size == 1) {
            return serviceInstances.instances[0]
        }

        val request = ProcessLoadBalanceRequest()
        request.dstInstances = serviceInstances

        val response = routerAPI.processLoadBalance(request)
        return response.targetInstance
    }

    fun doRoute(serviceInstances: ServiceInstances?, target: ServiceKey, args: PickSubchannelArgs): ServiceInstances {
        val request = ProcessRoutersRequest()
        request.dstInstances = serviceInstances

        val serviceInfo = SourceService()
        var source: ServiceKey? = null
        if (Objects.nonNull(sourceService)) {
            source = ServiceKey(sourceService!!.namespace, sourceService.service)
            serviceInfo.namespace = sourceService.namespace
            serviceInfo.service = sourceService.service
        }

        serviceInfo.arguments = collectRoutingLabels(loadRouteRule(target, source), args)
        request.sourceService = serviceInfo

        val response = routerAPI.processRouters(request)

        return response.serviceInstances
    }

    private fun collectRoutingLabels(routes: List<RoutingProto.Route>, args: PickSubchannelArgs): Set<RouteArgument> {
        val labelKeys: MutableSet<String> = HashSet()
        routes.forEach(Consumer { route: RoutingProto.Route ->
            for (source in route.sourcesList) {
                labelKeys.addAll(source.metadataMap.keys)
            }
        })

        val arguments: MutableSet<RouteArgument> = HashSet()
        val headers = args.headers

        labelKeys.forEach(Consumer<String> { labelKey: String ->
            if (StringUtils.equals(labelKey, RouteArgument.LABEL_KEY_PATH)) {
                arguments.add(RouteArgument.buildPath(args.methodDescriptor.fullMethodName))
                return@forEach
            }
            if (labelKey.startsWith(RouteArgument.LABEL_KEY_HEADER)) {
                val headerKey = labelKey.substring(RouteArgument.LABEL_KEY_HEADER.length + 1)
                arguments.add(
                    RouteArgument.buildHeader(
                        headerKey,
                        headers.get(Metadata.Key.of(headerKey, Metadata.ASCII_STRING_MARSHALLER))
                    )
                )
                return@forEach
            }
            if (labelKey.startsWith(RouteArgument.LABEL_KEY_CALLER_IP)) {
                arguments.add(RouteArgument.buildCallerIP(context.config.global.api.bindIP))
            }
        })

        return PolarisHelper.getLabelsInject().modifyRoute(HashSet<RouteArgument>(arguments))
    }

    private fun loadRouteRule(target: ServiceKey, source: ServiceKey?): List<RoutingProto.Route> {
        val rules: MutableList<RoutingProto.Route> = ArrayList()

        val inBoundReq = GetServiceRuleRequest()
        inBoundReq.service = target.service
        inBoundReq.namespace = target.namespace
        inBoundReq.ruleType = ServiceEventKey.EventType.ROUTING

        val inBoundResp = consumerAPI.getServiceRule(inBoundReq)
        val inBoundRule = inBoundResp.serviceRule.rule as Routing
        if (Objects.nonNull(inBoundRule)) {
            val tmpRules = RouteResp(inBoundRule.inboundsList, target).doFilter()
            rules.addAll(tmpRules)
        }

        if (Objects.isNull(source)) {
            return rules
        }

        val outBoundReq = GetServiceRuleRequest()
        outBoundReq.service = source!!.service
        outBoundReq.namespace = source.namespace
        outBoundReq.ruleType = ServiceEventKey.EventType.ROUTING

        val outBoundResp = consumerAPI.getServiceRule(outBoundReq)
        val outBoundRule = outBoundResp.serviceRule.rule as Routing
        if (Objects.nonNull(outBoundRule)) {
            val tmpRules = RouteResp(outBoundRule.outboundsList, source).doFilter()
            rules.addAll(tmpRules)
        }
        return rules
    }

    class EmptyPicker internal constructor(status: Status?) : SubchannelPicker() {
        private val status: Status = Preconditions.checkNotNull(status, "status")

        override fun pickSubchannel(args: PickSubchannelArgs): PickResult {
            return if (status.isOk) PickResult.withNoResult() else PickResult.withError(this.status)
        }
    }

    private class RouteResp(val rule: List<RoutingProto.Route>, val serviceKey: ServiceKey?) {
        fun doFilter(): List<RoutingProto.Route> {
            return rule.stream().filter { route: RoutingProto.Route ->
                for (source in route.sourcesList) {
                    if (source.namespace.value == RuleUtils.MATCH_ALL
                        && source.service.value == RuleUtils.MATCH_ALL
                    ) {
                        return@filter true
                    }

                    if (source.namespace.value == RuleUtils.MATCH_ALL
                        && source.service.value == serviceKey!!.service
                    ) {
                        return@filter true
                    }

                    if (source.namespace.value == serviceKey!!.namespace
                        && source.service.value == serviceKey.service
                    ) {
                        return@filter true
                    }
                }
                false
            }.toList()
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(PolarisPicker::class.java)
    }
}
