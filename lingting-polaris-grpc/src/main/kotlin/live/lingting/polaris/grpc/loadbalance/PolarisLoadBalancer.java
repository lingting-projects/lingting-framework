
package live.lingting.polaris.grpc.loadbalance;

import com.google.common.base.Preconditions;
import com.tencent.polaris.api.core.ConsumerAPI;
import com.tencent.polaris.api.pojo.ServiceKey;
import com.tencent.polaris.client.api.SDKContext;
import com.tencent.polaris.factory.api.DiscoveryAPIFactory;
import com.tencent.polaris.factory.api.RouterAPIFactory;
import com.tencent.polaris.router.api.core.RouterAPI;
import io.grpc.Attributes;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.Status;
import live.lingting.polaris.grpc.loadbalance.PolarisPicker.EmptyPicker;
import live.lingting.polaris.grpc.util.Common;
import live.lingting.polaris.grpc.util.GrpcHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.grpc.ConnectivityState.CONNECTING;
import static io.grpc.ConnectivityState.IDLE;
import static io.grpc.ConnectivityState.READY;
import static io.grpc.ConnectivityState.TRANSIENT_FAILURE;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class PolarisLoadBalancer extends LoadBalancer {

	private static final Status EMPTY_OK = Status.OK.withDescription("no subChannels ready");

	private final SDKContext context;

	private final ConsumerAPI consumerAPI;

	private final RouterAPI routerAPI;

	private final Helper helper;

	private final AtomicReference<ConnectivityState> currentState = new AtomicReference<>(IDLE);

	private final Map<String, Tuple<EquivalentAddressGroup, PolarisSubChannel>> subChannels = new ConcurrentHashMap<>();

	private final Predicate<ConnectivityState> predicate = state -> {
		if (state == READY) {
			return true;
		}

		return state != currentState.get();
	};

	private ServiceKey sourceService;

	private final Function<EquivalentAddressGroup, Tuple<EquivalentAddressGroup, PolarisSubChannel>> function;

	public PolarisLoadBalancer(final SDKContext context, final Helper helper) {
		this.context = context;
		this.consumerAPI = DiscoveryAPIFactory.createConsumerAPIByContext(context);
		this.routerAPI = RouterAPIFactory.createRouterAPIByContext(context);
		this.helper = Preconditions.checkNotNull(helper);
		this.function = addressGroup -> {
			Attributes newAttributes = addressGroup.getAttributes()
				.toBuilder()
				.set(GrpcHelper.STATE_INFO, new GrpcHelper.Ref<>(ConnectivityStateInfo.forNonError(IDLE)))
				.build();

			final Subchannel subChannel = helper.createSubchannel(
				CreateSubchannelArgs.newBuilder().setAddresses(addressGroup).setAttributes(newAttributes).build());

			subChannel.start(state -> processSubChannelState(subChannel, state));
			subChannel.requestConnection();

			PolarisSubChannel channel = new PolarisSubChannel(subChannel, newAttributes.get(Common.INSTANCE_KEY));
			return new Tuple<>(addressGroup, channel);
		};
	}

	@Override
	public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
		if (Objects.isNull(sourceService)) {
			this.sourceService = resolvedAddresses.getAttributes().get(Common.SOURCE_SERVICE_INFO);
		}

		List<EquivalentAddressGroup> servers = resolvedAddresses.getAddresses();
		if (servers.isEmpty()) {
			handleNameResolutionError(Status.NOT_FOUND);
			return;
		}

		Map<String, EquivalentAddressGroup> serversMap = servers.stream()
			.collect(HashMap::new, (m, e) -> m.put(buildKey(e), e), HashMap::putAll);
		Set<String> removed = GrpcHelper.setsDifference(subChannels.keySet(), serversMap.keySet());

		synchronized (subChannels) {
			for (EquivalentAddressGroup addressGroup : servers) {
				String key = buildKey(addressGroup);
				if (subChannels.containsKey(key)) {
					Tuple<EquivalentAddressGroup, PolarisSubChannel> value = subChannels.get(key);
					// 更新实例的状态信息到 SubChannel 中
					value.getB().setInstance(addressGroup.getAttributes().get(Common.INSTANCE_KEY));
				}
				else {
					subChannels.put(key, function.apply(addressGroup));
				}
			}
		}

		removed.forEach(entry -> {
			Subchannel channel = subChannels.remove(entry).getB();
			GrpcHelper.shutdownSubChannel(channel);
		});

	}

	@Override
	public void handleNameResolutionError(Status error) {
		if (currentState.get() != READY) {
			updateBalancingState(TRANSIENT_FAILURE, new EmptyPicker(error));
		}
	}

	private void processSubChannelState(Subchannel subChannel, ConnectivityStateInfo stateInfo) {
		Tuple<EquivalentAddressGroup, PolarisSubChannel> tuple = subChannels.get(buildKey(subChannel.getAddresses()));
		if (Objects.isNull(tuple)) {
			return;
		}
		PolarisSubChannel channel = tuple.getB();
		if (Objects.isNull(channel) || channel.getChannel() != subChannel) {
			return;
		}
		if (stateInfo.getState() == TRANSIENT_FAILURE || stateInfo.getState() == IDLE) {
			helper.refreshNameResolution();
		}
		if (stateInfo.getState() == IDLE) {
			subChannel.requestConnection();
		}
		GrpcHelper.Ref<ConnectivityStateInfo> subChannelStateRef = GrpcHelper.getSubChannelStateInfoRef(subChannel);
		if (subChannelStateRef.getValue().getState().equals(TRANSIENT_FAILURE)
			&& (stateInfo.getState().equals(CONNECTING) || stateInfo.getState().equals(IDLE))) {
			return;
		}
		subChannelStateRef.setValue(stateInfo);
		updateBalancingState();
	}

	private void updateBalancingState() {
		AtomicReference<Attributes> holder = new AtomicReference<>();
		Map<PolarisSubChannel, PolarisSubChannel> activeList = GrpcHelper.filterNonFailingSubChannels(subChannels,
			holder);
		if (activeList.isEmpty()) {
			boolean isConnecting = false;
			Status aggStatus = EMPTY_OK;
			for (Tuple<EquivalentAddressGroup, PolarisSubChannel> tuple : subChannels.values()) {
				Subchannel subchannel = tuple.getB();
				ConnectivityStateInfo stateInfo = GrpcHelper.getSubChannelStateInfoRef(subchannel).getValue();
				if (stateInfo.getState() == CONNECTING || stateInfo.getState() == IDLE) {
					isConnecting = true;
				}
				if (aggStatus == EMPTY_OK || !aggStatus.isOk()) {
					aggStatus = stateInfo.getStatus();
				}
			}
			updateBalancingState(isConnecting ? CONNECTING : TRANSIENT_FAILURE, new EmptyPicker(aggStatus));
		}
		else {
			updateBalancingState(READY, new PolarisPicker(activeList, context, this.consumerAPI, this.routerAPI,
				sourceService, holder.get()));
		}
	}

	private void updateBalancingState(ConnectivityState state, SubchannelPicker picker) {
		if (predicate.test(state)) {
			helper.updateBalancingState(state, picker);
			currentState.set(state);
		}
	}

	@Override
	public void shutdown() {
		//
	}

	private String buildKey(EquivalentAddressGroup group) {
		StringBuilder builder = new StringBuilder();
		builder.append(group.getAttributes().get(Common.TARGET_NAMESPACE_KEY));
		builder.append(group.getAttributes().get(Common.TARGET_SERVICE_KEY));
		group.getAddresses().forEach(builder::append);
		return builder.toString();
	}

	public static class Tuple<A, B> {

		private final A a;

		private final B b;

		public Tuple(A a, B b) {
			this.a = a;
			this.b = b;
		}

		public A getA() {
			return a;
		}

		public B getB() {
			return b;
		}

	}

}
