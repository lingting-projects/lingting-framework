package live.lingting.framework.grpc;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import live.lingting.framework.Sequence;
import live.lingting.framework.grpc.properties.GrpcClientProperties;
import live.lingting.framework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author lingting 2023-12-15 14:44
 */
public class GrpcClientProvide {

	protected final GrpcClientProperties properties;

	protected final List<ClientInterceptor> interceptors;

	public GrpcClientProvide(GrpcClientProperties properties, List<ClientInterceptor> interceptors) {
		this.properties = properties;
		this.interceptors = interceptors;
	}

	// region builder

	/**
	 * 直接使用默认配置构建
	 */
	public GrpcChannelBuilder builder() {
		return builder(properties.getHost(), properties.getPort()).provide();
	}

	public GrpcChannelBuilder builder(String target) {
		GrpcChannelBuilder builder = new GrpcChannelBuilder(this);
		return builder.target(target).provide();
	}

	/**
	 * 使用自定义配置构建, 不会自动加入配置
	 */
	public GrpcChannelBuilder builder(String host, Integer port) {
		GrpcChannelBuilder builder = new GrpcChannelBuilder(this);
		return builder.address(host, port);
	}

	public void addInterceptors(ManagedChannelBuilder<?> builder) {
		addInterceptors(builder, interceptors);
	}

	public void addInterceptors(ManagedChannelBuilder<?> builder, Collection<ClientInterceptor> collection) {
		if (CollectionUtils.isEmpty(collection)) {
			return;
		}

		// 升序排序
		List<ClientInterceptor> asc = Sequence.asc(collection);
		// 注册拦截器
		builder.intercept(asc);
	}

	public void useProperties(ManagedChannelBuilder<?> builder) {
		useProperties(builder, properties);
	}


	public void useProperties(ManagedChannelBuilder<?> builder, GrpcClientProperties properties) {
		// 开启心跳
		if (properties.isEnableKeepAlive()) {
			builder.keepAliveTime(properties.getKeepAliveTime().toMillis(), TimeUnit.MILLISECONDS)
				.keepAliveTimeout(properties.getKeepAliveTimeout().toMillis(), TimeUnit.MILLISECONDS);
		}

		// 使用明文
		if (properties.isUsePlaintext()) {
			builder.usePlaintext();
		}

		// 重试
		if (properties.isEnableRetry()) {
			builder.enableRetry();
		}

		// ssl配置
		if (!properties.isUsePlaintext() && properties.isDisableSsl()
			&& builder instanceof NettyChannelBuilder nettyChannelBuilder) {
			SslContextBuilder sslContextBuilder = GrpcSslContexts.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE);
			nettyChannelBuilder.sslContext(sslContextBuilder.build());
		}

	}

	// endregion

	public ManagedChannel channel() {
		return builder().build();
	}

	public ManagedChannel channel(String host, Integer port) {
		return channel(host, port, builder -> builder);
	}

	public ManagedChannel channel(String host, Integer port, UnaryOperator<ManagedChannelBuilder<?>> operator) {
		return builder(host, port).provide().build(operator);
	}

	public ManagedChannel channel(ManagedChannelBuilder<?> builder) {
		return channel(builder, interceptors);
	}

	public ManagedChannel channel(ManagedChannelBuilder<?> builder, List<ClientInterceptor> list) {
		useProperties(builder);
		addInterceptors(builder, list);
		return builder.build();
	}

	public <R extends AbstractStub<R>> R stub(Channel channel, Function<Channel, R> function) {
		return function.apply(channel);
	}

	public <R extends AbstractAsyncStub<R>> R async(Channel channel, Function<Channel, R> function) {
		return stub(channel, function);
	}

	public <R extends AbstractBlockingStub<R>> R blocking(Channel channel, Function<Channel, R> function) {
		return stub(channel, function);
	}

	public <R extends AbstractFutureStub<R>> R future(Channel channel, Function<Channel, R> function) {
		return stub(channel, function);
	}

}
