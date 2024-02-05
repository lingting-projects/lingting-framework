package live.lingting.framework.grpc.interceptor;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import live.lingting.framework.Sequence;
import live.lingting.framework.grpc.properties.GrpcClientProperties;
import live.lingting.framework.grpc.simple.ForwardingClientOnCall;
import live.lingting.framework.util.MdcUtils;
import live.lingting.framework.util.StringUtils;

/**
 * @author lingting 2023-04-13 13:23
 */
@SuppressWarnings("java:S110")
public class GrpcClientTraceIdInterceptor implements ClientInterceptor, Sequence {

	private final Metadata.Key<String> traceIdKey;

	public GrpcClientTraceIdInterceptor(GrpcClientProperties properties) {
		this.traceIdKey = Metadata.Key.of(properties.getTraceIdKey(), Metadata.ASCII_STRING_MARSHALLER);
	}

	/**
	 * 获取当前上下文的traceId
	 */
	protected String traceId() {
		return MdcUtils.getTraceId();
	}

	@Override
	public <S, R> ClientCall<S, R> interceptCall(MethodDescriptor<S, R> method, CallOptions callOptions, Channel next) {
		String traceId = traceId();

		ClientCall<S, R> call = next.newCall(method, callOptions);

		return new ForwardingClientOnCall<>(call) {
			@Override
			public void onStartBefore(Listener<R> responseListener, Metadata headers) {
				if (StringUtils.hasText(traceId)) {
					headers.put(traceIdKey, traceId);
				}
			}
		};

	}

	@Override
	public int getSequence() {
		return Integer.MIN_VALUE;
	}

}
