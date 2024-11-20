
package live.lingting.polaris.grpc.resolver;

import com.tencent.polaris.api.core.ConsumerAPI;
import com.tencent.polaris.api.listener.ServiceListener;
import com.tencent.polaris.api.pojo.Instance;
import com.tencent.polaris.api.pojo.ServiceChangeEvent;
import com.tencent.polaris.api.pojo.ServiceInstances;
import com.tencent.polaris.api.pojo.ServiceKey;
import com.tencent.polaris.api.rpc.GetHealthyInstancesRequest;
import com.tencent.polaris.api.rpc.InstancesResponse;
import com.tencent.polaris.api.rpc.UnWatchServiceRequest;
import com.tencent.polaris.api.rpc.WatchServiceRequest;
import com.tencent.polaris.client.api.SDKContext;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.polaris.grpc.util.Common;
import live.lingting.polaris.grpc.util.NetworkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Service discovery class
 *
 * @author lixiaoshuang
 */
public class PolarisNameResolver extends NameResolver {

	private static final Logger LOG = LoggerFactory.getLogger(PolarisNameResolver.class);

	private static final String DEFAULT_NAMESPACE = "default";

	private final ConsumerAPI consumerAPI;

	private final String namespace;

	private final String service;

	private final URI targetUri;

	private final SDKContext context;

	private final List<ResolverInterceptor> interceptors = new ArrayList<>();

	private ServiceChangeWatcher watcher;

	private ServiceKey sourceService;

	public PolarisNameResolver(URI targetUri, SDKContext context, ConsumerAPI consumerAPI) {
		this.targetUri = targetUri;
		Map<String, String> params = NetworkHelper.getUrlParams(targetUri.getQuery());
		this.service = targetUri.getHost();
		this.namespace = params.get("namespace") == null ? DEFAULT_NAMESPACE : params.get("namespace");
		this.context = context;
		this.consumerAPI = consumerAPI;
		ServiceLoader.load(ResolverInterceptor.class).iterator().forEachRemaining(interceptors::add);
		interceptors.sort(Comparator.comparingInt(ResolverInterceptor::priority));
		if (params.containsKey("extend_info")) {
			String json = new String(Base64.getUrlDecoder().decode(params.get("extend_info")));
			this.sourceService = JacksonUtils.toObj(json, ServiceKey.class);
		}
	}

	@Override
	public String getServiceAuthority() {
		return service;
	}

	@Override
	public void start(Listener2 listener) {
		doResolve(listener);
		doWatch(listener);
	}

	private void doResolve(Listener2 listener) {
		ResolverContext resolverContext = ResolverContext.builder()
			.context(context)
			.targetUri(targetUri)
			.sourceService(sourceService)
			.build();
		interceptors.forEach(resolverInterceptor -> resolverInterceptor.before(resolverContext));

		GetHealthyInstancesRequest request = new GetHealthyInstancesRequest();
		request.setNamespace(namespace);
		request.setService(service);
		InstancesResponse response = consumerAPI.getHealthyInstances(request);
		LOG.info("[grpc-polaris] namespace:{} service:{} instance size:{}", namespace, service,
			response.getInstances().length);

		for (ResolverInterceptor interceptor : interceptors) {
			response = interceptor.after(resolverContext, response);
		}

		LOG.info("[grpc-polaris] after namespace:{} service:{} instance size:{}", namespace, service,
			response.getInstances().length);
		notifyListener(listener, response);
	}

	private void doWatch(Listener2 listener) {
		this.watcher = new ServiceChangeWatcher(listener);
		this.consumerAPI.watchService(WatchServiceRequest.builder()
			.namespace(namespace)
			.service(service)
			.listeners(Collections.singletonList(this.watcher))
			.build());
	}

	private void notifyListener(Listener2 listener, InstancesResponse response) {
		ServiceInstances serviceInstances = response.toServiceInstances();
		List<EquivalentAddressGroup> equivalentAddressGroups = new ArrayList<>();
		for (Instance instance : serviceInstances.getInstances()) {
			if (Objects.equals("grpc", instance.getProtocol())) {
				equivalentAddressGroups.add(buildEquivalentAddressGroup(instance));
			}
		}

		Attributes.Builder builder = Attributes.newBuilder();

		if (sourceService != null) {
			builder.set(Common.SOURCE_SERVICE_INFO, sourceService);
		}

		listener.onResult(ResolutionResult.newBuilder()
			.setAddresses(equivalentAddressGroups)
			.setAttributes(builder.build())
			.build());
	}

	@Override
	public void shutdown() {
		if (this.watcher != null) {
			this.consumerAPI.unWatchService(UnWatchServiceRequest.UnWatchServiceRequestBuilder.anUnWatchServiceRequest()
				.listeners(Collections.singletonList(this.watcher))
				.namespace(namespace)
				.service(service)
				.build());
		}
	}

	private EquivalentAddressGroup buildEquivalentAddressGroup(Instance instance) {
		InetSocketAddress address = new InetSocketAddress(instance.getHost(), instance.getPort());
		Attributes attributes = Attributes.newBuilder()
			.set(Common.INSTANCE_KEY, instance)
			.set(Common.TARGET_NAMESPACE_KEY, namespace)
			.set(Common.TARGET_SERVICE_KEY, service)
			.build();
		return new EquivalentAddressGroup(address, attributes);
	}

	private class ServiceChangeWatcher implements ServiceListener {

		private final Listener2 listener;

		ServiceChangeWatcher(Listener2 listener) {
			this.listener = listener;
		}

		@Override
		public void onEvent(ServiceChangeEvent event) {
			doResolve(listener);
		}

	}

}
