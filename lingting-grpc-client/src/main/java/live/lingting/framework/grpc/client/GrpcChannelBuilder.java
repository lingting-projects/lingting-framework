package live.lingting.framework.grpc.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;
import live.lingting.framework.grpc.client.properties.GrpcClientProperties;
import live.lingting.framework.util.StringUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author lingting 2024-01-30 13:55
 */
@RequiredArgsConstructor
public class GrpcChannelBuilder {

	protected final GrpcClientProvide provide;

	private final List<ClientInterceptor> interceptors = new ArrayList<>();

	protected String target;

	protected GrpcClientProperties properties;

	public GrpcChannelBuilder address(String host, Integer port) {
		if (!StringUtils.hasText(host)) {
			throw new IllegalArgumentException("Host [%s] is invalid!".formatted(host));
		}
		if (port == null || port < 0 || port > 65535) {
			throw new IllegalArgumentException("Port [%d] is invalid!".formatted(port));
		}
		this.target = "%s:%d".formatted(host, port);
		return this;
	}

	public GrpcChannelBuilder target(String target) {
		this.target = target;
		return this;
	}

	public GrpcChannelBuilder interceptor() {
		return interceptor(provide.interceptors);
	}

	public GrpcChannelBuilder interceptor(ClientInterceptor interceptor) {
		interceptors.add(interceptor);
		return this;
	}

	public GrpcChannelBuilder interceptor(Collection<ClientInterceptor> collection) {
		interceptors.addAll(collection);
		return this;
	}

	/**
	 * 使用 provide的配置填充
	 */
	public GrpcChannelBuilder provide() {
		return interceptor().properties();
	}

	public GrpcChannelBuilder properties() {
		return properties(provide.properties);
	}

	public GrpcChannelBuilder properties(GrpcClientProperties properties) {
		this.properties = properties;
		return this;
	}

	public ManagedChannel build() {
		return build((UnaryOperator<ManagedChannelBuilder<?>>) builder -> builder);
	}

	public ManagedChannel build(UnaryOperator<ManagedChannelBuilder<?>> operator) {
		return build((Function<String, ManagedChannelBuilder<?>>) forTarget -> {
			ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forTarget(forTarget);
			return operator.apply(builder);
		});
	}

	public ManagedChannel build(Function<String, ManagedChannelBuilder<?>> function) {
		ManagedChannelBuilder<?> builder = function.apply(target);
		if (properties != null) {
			provide.useProperties(builder, properties);
		}
		provide.addInterceptors(builder, interceptors);
		return builder.build();
	}

	public <R extends AbstractStub<R>> R stub(Function<ManagedChannel, R> function) {
		ManagedChannel channel = build();
		return function.apply(channel);
	}

	public <R extends AbstractAsyncStub<R>> R async(Function<ManagedChannel, R> function) {
		return stub(function);
	}

	public <R extends AbstractBlockingStub<R>> R blocking(Function<ManagedChannel, R> function) {
		return stub(function);
	}

	public <R extends AbstractFutureStub<R>> R future(Function<ManagedChannel, R> function) {
		return stub(function);
	}

}
