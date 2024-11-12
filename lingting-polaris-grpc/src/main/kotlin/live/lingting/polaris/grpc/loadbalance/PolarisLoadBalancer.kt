package live.lingting.polaris.grpc.loadbalance

import com.google.common.base.Preconditions
import com.tencent.polaris.api.core.ConsumerAPI
import com.tencent.polaris.api.pojo.Instance
import com.tencent.polaris.api.pojo.ServiceKey
import com.tencent.polaris.client.api.SDKContext
import com.tencent.polaris.factory.api.DiscoveryAPIFactory
import com.tencent.polaris.factory.api.RouterAPIFactory
import com.tencent.polaris.router.api.core.RouterAPI
import io.grpc.Attributes
import io.grpc.ConnectivityState
import io.grpc.ConnectivityStateInfo
import io.grpc.EquivalentAddressGroup
import io.grpc.LoadBalancer
import io.grpc.Status
import live.lingting.polaris.grpc.util.Common
import live.lingting.polaris.grpc.util.GrpcHelper
import java.net.SocketAddress
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisLoadBalancer(private val context: SDKContext, helper: Helper) : LoadBalancer() {
    private val consumerAPI: ConsumerAPI = DiscoveryAPIFactory.createConsumerAPIByContext(context)

    private val routerAPI: RouterAPI = RouterAPIFactory.createRouterAPIByContext(context)

    private val helper: Helper = Preconditions.checkNotNull(helper)

    private val currentState = AtomicReference(ConnectivityState.IDLE)

    private val subChannels: MutableMap<String, Tuple<EquivalentAddressGroup, PolarisSubChannel>> = ConcurrentHashMap()

    private val predicate = Predicate { state: ConnectivityState ->
        if (state == ConnectivityState.READY) {
            return@Predicate true
        }
        state != currentState.get()
    }

    private var sourceService: ServiceKey? = null

    private val function: Function<EquivalentAddressGroup, Tuple<EquivalentAddressGroup, PolarisSubChannel>>

    init {
        this.function = Function<EquivalentAddressGroup, Tuple<EquivalentAddressGroup, PolarisSubChannel>> { addressGroup: EquivalentAddressGroup ->
            val newAttributes = addressGroup.attributes
                .toBuilder()
                .set<GrpcHelper.Ref<ConnectivityStateInfo>>(GrpcHelper.Companion.STATE_INFO, GrpcHelper.Ref<ConnectivityStateInfo>(ConnectivityStateInfo.forNonError(ConnectivityState.IDLE)))
                .build()
            val subChannel = helper.createSubchannel(
                CreateSubchannelArgs.newBuilder().setAddresses(addressGroup).setAttributes(newAttributes).build()
            )

            subChannel.start { state: ConnectivityStateInfo -> processSubChannelState(subChannel, state) }
            subChannel.requestConnection()

            val channel = PolarisSubChannel(subChannel, newAttributes.get<Instance>(Common.Companion.INSTANCE_KEY))
            Tuple<EquivalentAddressGroup, PolarisSubChannel>(addressGroup, channel)
        }
    }

    override fun handleResolvedAddresses(resolvedAddresses: ResolvedAddresses) {
        if (Objects.isNull(sourceService)) {
            this.sourceService = resolvedAddresses.attributes.get<ServiceKey>(Common.Companion.SOURCE_SERVICE_INFO)
        }

        val servers = resolvedAddresses.addresses
        if (servers.isEmpty()) {
            handleNameResolutionError(Status.NOT_FOUND)
            return
        }

        val serversMap: Map<String, EquivalentAddressGroup> = servers.stream()
            .collect({ HashMap() }, { m: java.util.HashMap<String, EquivalentAddressGroup>, e: EquivalentAddressGroup -> m[buildKey(e)] = e }, { obj: java.util.HashMap<String, EquivalentAddressGroup>, m: java.util.HashMap<String, EquivalentAddressGroup>? -> obj.putAll(m!!) })
        val removed: Set<String> = GrpcHelper.Companion.setsDifference<String>(subChannels.keys, serversMap.keys)

        synchronized(subChannels) {
            for (addressGroup in servers) {
                val key = buildKey(addressGroup)
                if (subChannels.containsKey(key)) {
                    val value = subChannels[key]!!
                    // 更新实例的状态信息到 SubChannel 中
                    value.b.instance = addressGroup.attributes.get<Instance>(Common.Companion.INSTANCE_KEY)
                } else {
                    subChannels[key] = function.apply(addressGroup)
                }
            }
        }

        removed.forEach(Consumer<String> { entry: String ->
            val channel: Subchannel = subChannels.remove(entry)!!.b
            GrpcHelper.Companion.shutdownSubChannel(channel)
        })
    }

    override fun handleNameResolutionError(error: Status) {
        if (currentState.get() != ConnectivityState.READY) {
            updateBalancingState(ConnectivityState.TRANSIENT_FAILURE, PolarisPicker.EmptyPicker(error))
        }
    }

    private fun processSubChannelState(subChannel: Subchannel, stateInfo: ConnectivityStateInfo) {
        val tuple = subChannels[buildKey(subChannel.addresses)]!!
        if (Objects.isNull(tuple)) {
            return
        }
        val channel = tuple.b
        if (Objects.isNull(channel) || channel.channel !== subChannel) {
            return
        }
        if (stateInfo.state == ConnectivityState.TRANSIENT_FAILURE || stateInfo.state == ConnectivityState.IDLE) {
            helper.refreshNameResolution()
        }
        if (stateInfo.state == ConnectivityState.IDLE) {
            subChannel.requestConnection()
        }
        val subChannelStateRef: GrpcHelper.Ref<ConnectivityStateInfo> = GrpcHelper.Companion.getSubChannelStateInfoRef(subChannel)
        if (subChannelStateRef.value.state == ConnectivityState.TRANSIENT_FAILURE
            && (stateInfo.state == ConnectivityState.CONNECTING || stateInfo.state == ConnectivityState.IDLE)
        ) {
            return
        }
        subChannelStateRef.setValue(stateInfo)
        updateBalancingState()
    }

    private fun updateBalancingState() {
        val holder = AtomicReference<Attributes?>()
        val activeList: Map<PolarisSubChannel?, PolarisSubChannel?> = GrpcHelper.Companion.filterNonFailingSubChannels(
            subChannels,
            holder
        )
        if (activeList.isEmpty()) {
            var isConnecting = false
            var aggStatus = EMPTY_OK
            for (tuple in subChannels.values) {
                val subchannel: Subchannel = tuple.b
                val stateInfo: ConnectivityStateInfo = GrpcHelper.Companion.getSubChannelStateInfoRef(subchannel).getValue()
                if (stateInfo.state == ConnectivityState.CONNECTING || stateInfo.state == ConnectivityState.IDLE) {
                    isConnecting = true
                }
                if (aggStatus === EMPTY_OK || !aggStatus.isOk) {
                    aggStatus = stateInfo.status
                }
            }
            updateBalancingState(if (isConnecting) ConnectivityState.CONNECTING else ConnectivityState.TRANSIENT_FAILURE, PolarisPicker.EmptyPicker(aggStatus))
        } else {
            updateBalancingState(
                ConnectivityState.READY, PolarisPicker(
                    activeList, context, this.consumerAPI, this.routerAPI,
                    sourceService, holder.get()
                )
            )
        }
    }

    private fun updateBalancingState(state: ConnectivityState, picker: SubchannelPicker) {
        if (predicate.test(state)) {
            helper.updateBalancingState(state, picker)
            currentState.set(state)
        }
    }

    override fun shutdown() {
        //
    }

    private fun buildKey(group: EquivalentAddressGroup): String {
        val builder = StringBuilder()
        builder.append(group.attributes.get<String>(Common.Companion.TARGET_NAMESPACE_KEY))
        builder.append(group.attributes.get<String>(Common.Companion.TARGET_SERVICE_KEY))
        group.addresses.forEach(Consumer { obj: SocketAddress? -> builder.append(obj) })
        return builder.toString()
    }

    class Tuple<A, B>(val a: A, val b: B)

    companion object {
        private val EMPTY_OK: Status = Status.OK.withDescription("no subChannels ready")
    }
}
