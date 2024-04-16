
package live.lingting.polaris.grpc.client;

import com.tencent.polaris.api.pojo.ServiceKey;
import com.tencent.polaris.client.api.SDKContext;
import io.grpc.BinaryLog;
import io.grpc.ClientInterceptor;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver.Factory;
import io.grpc.ProxyDetector;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.polaris.grpc.interceptor.PolarisClientInterceptor;
import live.lingting.polaris.grpc.loadbalance.PolarisLoadBalancerFactory;
import live.lingting.polaris.grpc.resolver.PolarisNameResolverFactory;
import live.lingting.polaris.grpc.util.JvmHookHelper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static live.lingting.polaris.grpc.loadbalance.PolarisLoadBalancerProvider.LOADBALANCER_PROVIDER;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
@SuppressWarnings({ "java:S2274", "java:S3010", "java:S2142", "java:S1874", "java:S3077" })
public class PolarisManagedChannelBuilder extends ManagedChannelBuilder<PolarisManagedChannelBuilder> {

	private static final AtomicBoolean FIRST_INIT = new AtomicBoolean(false);

	private static final Object MONITOR = new Object();

	private static volatile SDKContext sdkContext;

	private final ManagedChannelBuilder<?> builder;

	private final List<PolarisClientInterceptor> polarisInterceptors = new ArrayList<>();

	private final List<ClientInterceptor> interceptors = new ArrayList<>();

	private final ServiceKey sourceService;

	private PolarisManagedChannelBuilder(ServiceKey sourceService, SDKContext sdkContext,
			ManagedChannelBuilder<?> builder) {
		if (FIRST_INIT.compareAndSet(false, true)) {
			if (sdkContext == null) {
				sdkContext = SDKContext.initContext();
				JvmHookHelper.addShutdownHook(sdkContext::destroy);
			}
			PolarisLoadBalancerFactory.init(sdkContext);
			PolarisNameResolverFactory.init(sdkContext);
			PolarisManagedChannelBuilder.sdkContext = sdkContext;
			synchronized (MONITOR) {
				MONITOR.notifyAll();
			}
		}
		SDKContext prev = PolarisManagedChannelBuilder.sdkContext;
		if (prev == null) {
			synchronized (MONITOR) {
				try {
					MONITOR.wait();
				}
				catch (InterruptedException e) {
					// noop
				}
				prev = PolarisManagedChannelBuilder.sdkContext;
			}
		}
		if (sdkContext != null && prev != sdkContext) {
			throw new IllegalStateException("[Polaris] SDKContext already initialize");
		}

		this.builder = builder;
		this.sourceService = sourceService;
	}

	/**
	 * follow {@link ManagedChannelBuilder#forTarget(String)}
	 * @param target 服务名
	 * @return {@link PolarisManagedChannelBuilder}
	 */
	public static PolarisManagedChannelBuilder forTarget(String target) {
		return forTarget(target, null, null);
	}

	/**
	 * 增强 {@link ManagedChannelBuilder#forTarget(String)}, 在连接到目标服务时允许设置主调服务的相关信息
	 * @param target 服务名
	 * @param sourceService {@link ServiceKey} 主调服务信息以及标签
	 * @return {@link PolarisManagedChannelBuilder}
	 */
	public static PolarisManagedChannelBuilder forTarget(String target, ServiceKey sourceService) {
		return forTarget(target, sourceService, null);
	}

	/**
	 * 增强 {@link ManagedChannelBuilder#forTarget(String)}, 在连接到目标服务时允许设置主调服务的相关信息,
	 * 并且可以自定义北极星 SDK 的核心数据结构 {@link SDKContext}
	 * @param target 服务名
	 * @param sourceService {@link ServiceKey} 主调服务信息以及标签
	 * @param sdkContext {@link SDKContext} 可以设置北极星 SDK 的相关配置以及行为, 例如服务治理中心地址等等
	 * @return {@link PolarisManagedChannelBuilder}
	 */
	public static PolarisManagedChannelBuilder forTarget(String target, ServiceKey sourceService,
			SDKContext sdkContext) {
		ManagedChannelBuilder<?> forTarget = ManagedChannelBuilder.forTarget(buildUrl(target, sourceService));
		return forTarget(sourceService, sdkContext, forTarget);
	}

	public static PolarisManagedChannelBuilder forTarget(ServiceKey sourceService, SDKContext sdkContext,
			ManagedChannelBuilder<?> builder) {
		return new PolarisManagedChannelBuilder(sourceService, sdkContext, builder);
	}

	public static SDKContext getSDKContext() {
		return sdkContext;
	}

	static void resetSDKContext() {
		sdkContext = null;
	}

	public static String buildUrl(String target, ServiceKey sourceService) {
		if (Objects.isNull(sourceService)) {
			return target;
		}

		String json = JacksonUtils.toJson(sourceService);
		String extendInfo = Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

		if (target.contains("?")) {
			target += "&extend_info=" + extendInfo;
		}
		else {
			target += "?extend_info=" + extendInfo;
		}

		return target;
	}

	public PolarisManagedChannelBuilder directExecutor() {
		builder.directExecutor();
		return this;
	}

	public PolarisManagedChannelBuilder executor(Executor executor) {
		builder.executor(executor);
		return this;
	}

	public PolarisManagedChannelBuilder intercept(List<ClientInterceptor> interceptors) {

		for (ClientInterceptor interceptor : interceptors) {
			if (interceptor instanceof PolarisClientInterceptor polarisClientInterceptor) {
				this.polarisInterceptors.add(polarisClientInterceptor);
			}
			else {
				this.interceptors.add(interceptor);
			}
		}
		return this;
	}

	public PolarisManagedChannelBuilder intercept(ClientInterceptor... interceptors) {
		for (ClientInterceptor interceptor : interceptors) {
			if (interceptor instanceof PolarisClientInterceptor polarisClientInterceptor) {
				this.polarisInterceptors.add(polarisClientInterceptor);
			}
			else {
				this.interceptors.add(interceptor);
			}
		}
		return this;
	}

	public PolarisManagedChannelBuilder userAgent(String userAgent) {
		builder.userAgent(userAgent);
		return this;
	}

	public PolarisManagedChannelBuilder overrideAuthority(String authority) {
		builder.overrideAuthority(authority);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder nameResolverFactory(Factory resolverFactory) {
		builder.nameResolverFactory(resolverFactory);
		return this;
	}

	public PolarisManagedChannelBuilder decompressorRegistry(DecompressorRegistry registry) {
		builder.decompressorRegistry(registry);
		return this;
	}

	public PolarisManagedChannelBuilder compressorRegistry(CompressorRegistry registry) {
		builder.compressorRegistry(registry);
		return this;
	}

	public PolarisManagedChannelBuilder idleTimeout(long value, TimeUnit unit) {
		builder.idleTimeout(value, unit);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder offloadExecutor(Executor executor) {
		this.builder.offloadExecutor(executor);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder usePlaintext() {
		this.builder.usePlaintext();
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder useTransportSecurity() {
		this.builder.useTransportSecurity();
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder maxInboundMessageSize(int bytes) {
		this.builder.maxInboundMessageSize(bytes);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder maxInboundMetadataSize(int bytes) {
		this.builder.maxInboundMetadataSize(bytes);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder keepAliveTime(long keepAliveTime, TimeUnit timeUnit) {
		this.builder.keepAliveTime(keepAliveTime, timeUnit);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder keepAliveTimeout(long keepAliveTimeout, TimeUnit timeUnit) {
		this.builder.keepAliveTimeout(keepAliveTimeout, timeUnit);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder keepAliveWithoutCalls(boolean enable) {
		this.builder.keepAliveWithoutCalls(enable);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder maxRetryAttempts(int maxRetryAttempts) {
		this.builder.maxRetryAttempts(maxRetryAttempts);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder maxHedgedAttempts(int maxHedgedAttempts) {
		this.builder.maxHedgedAttempts(maxHedgedAttempts);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder retryBufferSize(long bytes) {
		this.builder.retryBufferSize(bytes);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder perRpcBufferLimit(long bytes) {
		this.builder.perRpcBufferLimit(bytes);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder disableRetry() {
		this.builder.disableRetry();
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder enableRetry() {
		this.builder.enableRetry();
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder setBinaryLog(BinaryLog binaryLog) {
		this.builder.setBinaryLog(binaryLog);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder maxTraceEvents(int maxTraceEvents) {
		this.builder.maxTraceEvents(maxTraceEvents);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder proxyDetector(ProxyDetector proxyDetector) {
		this.builder.proxyDetector(proxyDetector);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder defaultServiceConfig(Map<String, ?> serviceConfig) {
		this.builder.defaultServiceConfig(serviceConfig);
		return this;
	}

	@Override
	public PolarisManagedChannelBuilder disableServiceConfigLookUp() {
		this.builder.disableServiceConfigLookUp();
		return this;
	}

	public ManagedChannel build() {
		for (PolarisClientInterceptor clientInterceptor : polarisInterceptors) {
			clientInterceptor.init(this.sourceService.getNamespace(), this.sourceService.getService(), sdkContext);
			this.builder.intercept(clientInterceptor);
		}
		this.builder.intercept(interceptors);
		this.builder.defaultLoadBalancingPolicy(LOADBALANCER_PROVIDER);
		return builder.build();
	}

}
