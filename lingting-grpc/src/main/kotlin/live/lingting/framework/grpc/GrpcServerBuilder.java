package live.lingting.framework.grpc;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import live.lingting.framework.grpc.properties.GrpcServerProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;

/**
 * @author lingting 2024-01-30 15:37
 */
public class GrpcServerBuilder {

	protected List<BindableService> services = new ArrayList<>();

	protected List<ServerInterceptor> interceptors = new ArrayList<>();

	protected Integer port;

	protected GrpcServerProperties properties;

	public GrpcServerBuilder port(Integer port) {
		if (port == null || port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port [%d] is invalid!".formatted(port));
		}
		this.port = port;
		return this;
	}

	public GrpcServerBuilder service(BindableService interceptor) {
		services.add(interceptor);
		return this;
	}

	public GrpcServerBuilder service(Collection<BindableService> collection) {
		services.addAll(collection);
		return this;
	}

	public GrpcServerBuilder interceptor(ServerInterceptor interceptor) {
		interceptors.add(interceptor);
		return this;
	}

	public GrpcServerBuilder interceptor(Collection<ServerInterceptor> collection) {
		interceptors.addAll(collection);
		return this;
	}

	public GrpcServerBuilder properties(GrpcServerProperties properties) {
		this.properties = properties;
		if (properties.getPort() != null) {
			port(properties.getPort());
		}
		return this;
	}

	public GrpcServer build() {
		return build((UnaryOperator<ServerBuilder<?>>) builder -> builder);
	}

	public GrpcServer build(UnaryOperator<ServerBuilder<?>> operator) {
		return build((IntFunction<ServerBuilder<?>>) forPort -> {
			ServerBuilder<?> builder = ServerBuilder.forPort(forPort);
			return operator.apply(builder);
		});
	}

	public GrpcServer build(IntFunction<ServerBuilder<?>> function) {
		port(port);
		ServerBuilder<?> builder = function.apply(port);
		if (properties != null) {
			// 单个消息最大大小
			builder.maxInboundMessageSize((int) properties.getMessageSize())
				.keepAliveTime(properties.getKeepAliveTime().toMillis(), TimeUnit.MILLISECONDS)
				.keepAliveTimeout(properties.getKeepAliveTimeout().toMillis(), TimeUnit.MILLISECONDS);
		}
		return new GrpcServer(builder, interceptors, services);
	}

}
