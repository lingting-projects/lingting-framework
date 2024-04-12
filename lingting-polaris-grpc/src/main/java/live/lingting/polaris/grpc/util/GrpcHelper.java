
package live.lingting.polaris.grpc.util;

import io.grpc.Attributes;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer.Subchannel;
import live.lingting.polaris.grpc.loadbalance.PolarisLoadBalancer.Tuple;
import live.lingting.polaris.grpc.loadbalance.PolarisSubChannel;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.ConnectivityState.READY;
import static io.grpc.ConnectivityState.SHUTDOWN;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
@UtilityClass
public class GrpcHelper {

	public static final Attributes.Key<Ref<ConnectivityStateInfo>> STATE_INFO = Attributes.Key.create("state-info");

	public static <T> Set<T> setsDifference(Set<T> a, Set<T> b) {
		Set<T> aCopy = new HashSet<>(a);
		aCopy.removeAll(b);
		return aCopy;
	}

	public static Ref<ConnectivityStateInfo> getSubChannelStateInfoRef(Subchannel subchannel) {
		return checkNotNull(subchannel.getAttributes().get(STATE_INFO), "STATE_INFO");
	}

	static boolean isReady(Subchannel subchannel) {
		return getSubChannelStateInfoRef(subchannel).value.getState() == READY;
	}

	public static void shutdownSubChannel(Subchannel channel) {
		if (channel == null) {
			return;
		}

		channel.shutdown();
		getSubChannelStateInfoRef(channel).value = ConnectivityStateInfo.forNonError(SHUTDOWN);
	}

	public static Map<PolarisSubChannel, PolarisSubChannel> filterNonFailingSubChannels(
			Map<String, Tuple<EquivalentAddressGroup, PolarisSubChannel>> subChannels,
			AtomicReference<Attributes> attributeHolder) {
		Map<PolarisSubChannel, PolarisSubChannel> readySubChannels = new HashMap<>();

		subChannels.forEach((key, val) -> {
			PolarisSubChannel channel = val.getB();
			if (isReady(channel)) {
				attributeHolder.set(channel.getAttributes());
				readySubChannels.put(channel, channel);
			}
		});

		return readySubChannels;
	}

	@SuppressWarnings("java:S3077")
	public static final class Ref<T> {

		volatile T value;

		public Ref(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

	}

}
