package live.lingting.polaris.grpc.ratelimit

import com.tencent.polaris.api.core.ConsumerAPI
import com.tencent.polaris.api.pojo.ServiceEventKey
import com.tencent.polaris.api.pojo.ServiceKey
import com.tencent.polaris.api.rpc.GetServiceRuleRequest
import com.tencent.polaris.api.utils.StringUtils
import com.tencent.polaris.client.api.SDKContext
import com.tencent.polaris.factory.api.DiscoveryAPIFactory
import com.tencent.polaris.ratelimit.api.core.LimitAPI
import com.tencent.polaris.ratelimit.api.rpc.Argument
import com.tencent.polaris.ratelimit.api.rpc.QuotaRequest
import com.tencent.polaris.ratelimit.api.rpc.QuotaResponse
import com.tencent.polaris.ratelimit.api.rpc.QuotaResultCode
import com.tencent.polaris.ratelimit.factory.LimitAPIFactory
import com.tencent.polaris.specification.api.v1.traffic.manage.RateLimitProto
import com.tencent.polaris.specification.api.v1.traffic.manage.RateLimitProto.MatchArgument
import com.tencent.polaris.specification.api.v1.traffic.manage.RateLimitProto.RateLimit
import io.grpc.Grpc
import io.grpc.HttpConnectProxiedSocketAddress
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.Status
import live.lingting.polaris.grpc.interceptor.PolarisServerInterceptor
import live.lingting.polaris.grpc.util.Common
import live.lingting.polaris.grpc.util.PolarisHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.BiFunction
import java.util.function.Consumer

/**
 * gRPC-Server 端限流拦截器
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisRateLimitServerInterceptor : PolarisServerInterceptor() {
    private var limitAPI: LimitAPI? = null

    private var consumerAPI: ConsumerAPI? = null

    private var namespace: String? = "default"

    private var applicationName: String? = ""

    private var rateLimitCallback: BiFunction<QuotaResponse, String, Status>? = null

    override fun init(namespace: String?, applicationName: String?, context: SDKContext) {
        this.namespace = namespace
        this.applicationName = applicationName
        this.limitAPI = LimitAPIFactory.createLimitAPIByContext(context)
        this.consumerAPI = DiscoveryAPIFactory.createConsumerAPIByContext(context)
    }

    override fun <R, P> interceptCall(call: ServerCall<R, P>, headers: Metadata, next: ServerCallHandler<R, P>): ServerCall.Listener<R> {
        val aname = this.applicationName
        val applicationRegisterMode = StringUtils.isNotBlank(aname)
        val serviceName = if (applicationRegisterMode) aname else call.methodDescriptor.serviceName
        val method = if (applicationRegisterMode)
            call.methodDescriptor.fullMethodName
        else
            call.methodDescriptor.bareMethodName

        val request = QuotaRequest()
        request.namespace = namespace
        request.service = serviceName
        request.method = method
        request.count = 1

        val ruleResp = loadRateLimitRule(ServiceKey(namespace, serviceName))
        request.arguments = buildArguments(ruleResp, call, headers)

        LOG.debug("[grpc-polaris] do acquire rate-limit quota, request : {}", request)

        val response = limitAPI!!.getQuota(request)
        if (response.code == QuotaResultCode.QuotaResultOk) {
            return next.startCall(call, headers)
        }

        val errStatus = rateLimitCallback!!.apply(response, call.methodDescriptor.fullMethodName)
        call.close(errStatus, headers)
        return object : ServerCall.Listener<R>() {
        }
    }

    private fun <R, P> buildArguments(rateLimitResp: RateLimitResp, call: ServerCall<R, P>, headers: Metadata): Set<Argument> {
        val arguments: MutableSet<Argument> = HashSet()
        val matchArguments: MutableSet<MatchArgument> = HashSet()

        rateLimitResp.rules.forEach(Consumer<RateLimitProto.Rule> { rule: RateLimitProto.Rule ->
            if (rule.hasDisable()) {
                return@forEach
            }
            matchArguments.addAll(rule.argumentsList)
        })

        matchArguments.forEach(Consumer<MatchArgument> { argument: MatchArgument ->
            val key = argument.key
            when (argument.type) {
                MatchArgument.Type.HEADER -> arguments.add(
                    Argument.buildHeader(
                        argument.key,
                        headers.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))
                    )
                )

                MatchArgument.Type.CALLER_IP -> {
                    val clientAddress = call
                        .attributes
                        .get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR) as HttpConnectProxiedSocketAddress?
                    if (Objects.nonNull(clientAddress)) {
                        val address = clientAddress!!.targetAddress
                        if (Objects.nonNull(address)) {
                            arguments.add(Argument.buildCallerIP(address.address.hostAddress))
                        }
                    }
                }

                MatchArgument.Type.CALLER_SERVICE -> {
                    val callerNamespace = headers.get<String>(Common.Companion.CALLER_NAMESPACE_KEY)
                    val callerService = headers.get<String>(Common.Companion.CALLER_SERVICE_KEY)
                    arguments.add(Argument.buildCallerService(callerNamespace, callerService))
                }

                else -> {}
            }
        })

        return PolarisHelper.Companion.getLabelsInject().modifyRateLimit(arguments)
    }

    private fun loadRateLimitRule(target: ServiceKey): RateLimitResp {
        val inBoundReq = GetServiceRuleRequest()
        inBoundReq.service = target.service
        inBoundReq.namespace = target.namespace
        inBoundReq.ruleType = ServiceEventKey.EventType.RATE_LIMITING

        val inBoundResp = consumerAPI!!.getServiceRule(inBoundReq)
        val inBoundRule = inBoundResp.serviceRule.rule as RateLimit
        if (Objects.nonNull(inBoundRule)) {
            return RateLimitResp(inBoundRule.rulesList, target)
        }
        return RateLimitResp(emptyList(), null)
    }

    fun setRateLimitCallback(rateLimitCallback: BiFunction<QuotaResponse, String, Status>?) {
        this.rateLimitCallback = rateLimitCallback
    }

    private class RateLimitResp(val rules: List<RateLimitProto.Rule>, val serviceKey: ServiceKey?)

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(PolarisRateLimitServerInterceptor::class.java)
    }
}
