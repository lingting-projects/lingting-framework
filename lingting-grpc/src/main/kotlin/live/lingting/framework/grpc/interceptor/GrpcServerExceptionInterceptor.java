package live.lingting.framework.grpc.interceptor;

import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import live.lingting.framework.Sequence;
import live.lingting.framework.grpc.exception.GrpcExceptionInvoke;
import live.lingting.framework.grpc.exception.GrpcExceptionProcessor;
import live.lingting.framework.grpc.properties.GrpcServerProperties;

/**
 * @author lingting 2024-03-27 10:05
 */
public class GrpcServerExceptionInterceptor implements ServerInterceptor, Sequence {

	private final GrpcServerProperties properties;

	private final GrpcExceptionProcessor processor;

	public GrpcServerExceptionInterceptor(GrpcServerProperties properties, GrpcExceptionProcessor processor) {
		this.properties = properties;
		this.processor = processor;
	}

	@Override
	public <S, R> ServerCall.Listener<S> interceptCall(ServerCall<S, R> call, Metadata headers,
													   ServerCallHandler<S, R> next) {
		ServerCall.Listener<S> listener;
		try {
			ServerCall.Listener<S> nextCall = next.startCall(call, headers);
			listener = new SimpleForwardingServerCallListener<>(nextCall) {
				@Override
				public void onHalfClose() {
					try {
						super.onHalfClose();
					}
					catch (Exception e) {
						process(e, call, headers);
					}
				}
			};
		}
		catch (Exception e) {
			process(e, call, headers);
			listener = new ServerCall.Listener<>() {
			};
		}
		return listener;
	}

	@Override
	public int getSequence() {
		return properties.getExceptionHandlerOrder();
	}

	void process(Exception e, ServerCall<?, ?> call, Metadata headers) {
		GrpcExceptionInvoke invoke = processor.find(e);
		Object object = invoke.invoke(e, call, headers);
		if (object instanceof Status status) {
			call.close(status, headers);
		}
	}

}
