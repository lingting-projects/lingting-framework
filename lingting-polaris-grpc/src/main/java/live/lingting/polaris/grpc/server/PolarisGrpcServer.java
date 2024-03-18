
package live.lingting.polaris.grpc.server;

import com.tencent.polaris.api.core.ProviderAPI;
import com.tencent.polaris.api.exception.PolarisException;
import com.tencent.polaris.api.rpc.InstanceDeregisterRequest;
import com.tencent.polaris.api.rpc.InstanceHeartbeatRequest;
import com.tencent.polaris.api.rpc.InstanceRegisterRequest;
import com.tencent.polaris.api.rpc.InstanceRegisterResponse;
import com.tencent.polaris.api.utils.StringUtils;
import com.tencent.polaris.client.api.SDKContext;
import com.tencent.polaris.factory.api.DiscoveryAPIFactory;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import live.lingting.polaris.grpc.server.impl.NoopDelayRegister;
import live.lingting.polaris.grpc.util.NetworkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lixiaoshuang
 */
public class PolarisGrpcServer extends Server {

	private static final Logger LOG = LoggerFactory.getLogger(PolarisGrpcServer.class);

	private final SDKContext context;

	private final ProviderAPI providerAPI;

	private final PolarisGrpcServerBuilder builder;

	private Server targetServer;

	private String host;

	private DelayRegister delayRegister = new NoopDelayRegister();

	private Duration maxWaitDuration;

	private RegisterHook registerHook;

	private final AtomicBoolean shutdownOnce = new AtomicBoolean(false);

	private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2, r -> {
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.setName("polaris-grpc-server");
		return t;
	});

	PolarisGrpcServer(PolarisGrpcServerBuilder builder, SDKContext context, Server server) {
		this.builder = builder;
		this.registerHook = builder.getRegisterHook();
		this.targetServer = server;
		this.context = context;
		this.providerAPI = DiscoveryAPIFactory.createProviderAPIByContext(context);
	}

	@Override
	public Server start() throws IOException {
		initLocalHost();
		targetServer = targetServer.start();

		if (Objects.nonNull(delayRegister)) {
			executorService.execute(() -> {
				for (;;) {
					if (delayRegister.allowRegis()) {
						break;
					}
				}

				this.registerInstance(targetServer.getServices());
			});
		}

		return this;
	}

	@Override
	public Server shutdown() {
		if (shutdownOnce.compareAndSet(false, true)) {
			executorService.shutdownNow();
			// 将自己从注册中心反注册掉
			this.deregister(targetServer.getServices());
			providerAPI.destroy();
		}

		return new GraceOffline(targetServer, maxWaitDuration, context).shutdown();
	}

	@Override
	public Server shutdownNow() {
		if (shutdownOnce.compareAndSet(false, true)) {
			executorService.shutdownNow();
			this.deregister(targetServer.getServices());
			providerAPI.destroy();
			context.close();
		}
		return this.targetServer.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return this.targetServer.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return this.targetServer.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.targetServer.awaitTermination(timeout, unit);
	}

	@Override
	public void awaitTermination() throws InterruptedException {
		this.targetServer.awaitTermination();
	}

	public void setDelayRegister(DelayRegister delayRegister) {
		if (delayRegister == null) {
			return;
		}
		this.delayRegister = delayRegister;
	}

	public void setMaxWaitDuration(Duration maxWaitDuration) {
		this.maxWaitDuration = maxWaitDuration;
	}

	private void initLocalHost() {
		host = builder.getHost();
		if (StringUtils.isNotBlank(host)) {
			return;
		}
		String polarisServerAddr = context.getConfig().getGlobal().getServerConnector().getAddresses().get(0);
		String[] detail = polarisServerAddr.split(":");
		host = NetworkHelper.getLocalHost(detail[0], Integer.parseInt(detail[1]));
	}

	/**
	 * This interface will determine whether it is an interface-level registration
	 * instance or an application-level instance registration based on
	 * grpcServiceRegister.
	 */
	private void registerInstance(List<ServerServiceDefinition> definitions) {
		if (StringUtils.isNotBlank(builder.getApplicationName())) {
			this.registerOne(builder.getApplicationName());
			return;
		}
		for (ServerServiceDefinition definition : definitions) {
			String grpcServiceName = definition.getServiceDescriptor().getName();
			this.registerOne(grpcServiceName);
		}
	}

	/**
	 * Register a service instance.
	 * @param serviceName service name
	 */
	private void registerOne(String serviceName) {
		InstanceRegisterRequest request = new InstanceRegisterRequest();
		request.setNamespace(builder.getNamespace());
		request.setService(serviceName);
		request.setHost(host);
		request.setToken(builder.getToken());
		request.setVersion(builder.getVersion());
		request.setProtocol("grpc");
		request.setWeight(builder.getWeight());
		request.setPort(targetServer.getPort());
		request.setTtl(builder.getHeartbeatInterval());
		request.setMetadata(builder.getMetaData());

		if (Objects.nonNull(registerHook)) {
			registerHook.beforeRegister(request);
		}

		InstanceRegisterResponse response = providerAPI.register(request);

		if (Objects.nonNull(registerHook)) {
			registerHook.afterRegister(response);
		}

		LOG.info("[grpc-polaris] register polaris success, instance-id:{}", response.getInstanceId());

		this.heartBeat(serviceName);
	}

	/**
	 * Report heartbeat.
	 * @param serviceName service name
	 */
	private void heartBeat(String serviceName) {
		final int ttl = builder.getHeartbeatInterval();
		final int port = targetServer.getPort();
		final String namespace = builder.getNamespace();
		executorService.scheduleAtFixedRate(() -> {
			LOG.info("[grpc-polaris] report service heartbeat");
			InstanceHeartbeatRequest request = new InstanceHeartbeatRequest();
			request.setNamespace(namespace);
			request.setService(serviceName);
			request.setHost(host);
			request.setPort(port);
			try {
				providerAPI.heartbeat(request);
			}
			catch (PolarisException e) {
				LOG.error("[grpc-polaris] report service heartbeat fail", e);
			}
		}, ttl / 2, ttl, TimeUnit.SECONDS);
	}

	/**
	 * Service deregister.
	 * @param definitions Definition of a service
	 */
	private void deregister(List<ServerServiceDefinition> definitions) {
		LOG.info("[grpc-polaris] begin do deregister grpc service");
		if (StringUtils.isNotBlank(builder.getApplicationName())) {
			this.deregisterOne(builder.getApplicationName());
			return;
		}
		for (ServerServiceDefinition definition : definitions) {
			String grpcServiceName = definition.getServiceDescriptor().getName();
			this.deregisterOne(grpcServiceName);
		}
	}

	/**
	 * deregister a service instance.
	 * @param serviceName service name
	 */
	private void deregisterOne(String serviceName) {
		InstanceDeregisterRequest request = new InstanceDeregisterRequest();
		request.setNamespace(builder.getNamespace());
		request.setService(serviceName);
		request.setHost(host);
		request.setPort(targetServer.getPort());
		providerAPI.deRegister(request);
	}

}
