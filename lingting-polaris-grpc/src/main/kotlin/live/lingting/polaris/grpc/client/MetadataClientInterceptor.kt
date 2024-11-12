package live.lingting.polaris.grpc.client;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;
import live.lingting.polaris.grpc.metadata.MetadataContext;

import java.util.Objects;
import java.util.function.Predicate;

import static live.lingting.polaris.grpc.metadata.MetadataContext.METADATA_CONTEXT_KEY;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class MetadataClientInterceptor implements ClientInterceptor {

	private final Predicate<String> transitiveFilter;

	public MetadataClientInterceptor(Predicate<String> transitiveFilter) {
		Objects.requireNonNull(transitiveFilter, "transitiveFilter must not be null");
		this.transitiveFilter = transitiveFilter;
	}

	@Override
	public <R, P> ClientCall<R, P> interceptCall(MethodDescriptor<R, P> methodDescriptor, CallOptions callOptions,
			Channel channel) {
		return new SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)) {

			@Override
			public void start(Listener<P> responseListener, Metadata headers) {
				copyMetadataToHeader(headers);
				super.start(responseListener, headers);
			}

		};
	}

	private void copyMetadataToHeader(Metadata headers) {
		MetadataContext metadataContext = METADATA_CONTEXT_KEY.get();

		metadataContext.getHeaderFragment().forEach((key, val) -> {
			if (!transitiveFilter.test(key)) {
				return;
			}
			headers.put(Key.of(key, Metadata.ASCII_STRING_MARSHALLER), val);
		});

		metadataContext.getGrpcContextFragment().forEach((key, val) -> {
			if (!transitiveFilter.test(key)) {
				return;
			}
			headers.put(Key.of(key, Metadata.ASCII_STRING_MARSHALLER), val);
		});
	}

}
