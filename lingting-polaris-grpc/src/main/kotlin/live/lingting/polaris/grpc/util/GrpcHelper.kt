package live.lingting.polaris.grpc.util

import com.google.common.base.Preconditions
import io.grpc.Attributes
import io.grpc.ConnectivityState
import io.grpc.ConnectivityStateInfo
import io.grpc.EquivalentAddressGroup
import io.grpc.LoadBalancer
import live.lingting.polaris.grpc.loadbalance.PolarisLoadBalancer
import live.lingting.polaris.grpc.loadbalance.PolarisSubChannel
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.Volatile

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class GrpcHelper private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    class Ref<T>(@field:Volatile var value: T) {
        fun getValue(): T {
            return value
        }

        fun setValue(value: T) {
            this.value = value
        }
    }

    companion object {
        val STATE_INFO: Attributes.Key<Ref<ConnectivityStateInfo>> = Attributes.Key.create("state-info")

        fun <T> setsDifference(a: Set<T>, b: Set<T>): Set<T> {
            val aCopy: MutableSet<T> = HashSet(a)
            aCopy.removeAll(b)
            return aCopy
        }

        fun getSubChannelStateInfoRef(subchannel: LoadBalancer.Subchannel): Ref<ConnectivityStateInfo> {
            return Preconditions.checkNotNull(subchannel.attributes.get(STATE_INFO), "STATE_INFO")
        }

        fun isReady(subchannel: LoadBalancer.Subchannel): Boolean {
            return getSubChannelStateInfoRef(subchannel).value.state == ConnectivityState.READY
        }

        fun shutdownSubChannel(channel: LoadBalancer.Subchannel?) {
            if (channel == null) {
                return
            }

            channel.shutdown()
            getSubChannelStateInfoRef(channel).value = ConnectivityStateInfo.forNonError(ConnectivityState.SHUTDOWN)
        }

        fun filterNonFailingSubChannels(
            subChannels: Map<String, PolarisLoadBalancer.Tuple<EquivalentAddressGroup, PolarisSubChannel>>,
            attributeHolder: AtomicReference<Attributes?>
        ): Map<PolarisSubChannel?, PolarisSubChannel?> {
            val readySubChannels: MutableMap<PolarisSubChannel?, PolarisSubChannel?> = HashMap()

            subChannels.forEach { (key: String?, `val`: PolarisLoadBalancer.Tuple<EquivalentAddressGroup?, PolarisSubChannel?>?) ->
                val channel: PolarisSubChannel = `val`.getB()
                if (isReady(channel)) {
                    attributeHolder.set(channel.attributes)
                    readySubChannels[channel] = channel
                }
            }

            return readySubChannels
        }
    }
}
