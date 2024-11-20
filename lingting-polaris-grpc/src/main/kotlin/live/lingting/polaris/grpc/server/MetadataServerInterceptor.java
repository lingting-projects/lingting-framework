
package live.lingting.polaris.grpc.server;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import live.lingting.polaris.grpc.metadata.MetadataContext;

import java.util.Set;

import static live.lingting.polaris.grpc.metadata.MetadataContext.METADATA_CONTEXT_KEY;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class MetadataServerInterceptor implements ServerInterceptor {

	@Override
	public <R, P> Listener<R> interceptCall(ServerCall<R, P> serverCall, Metadata metadata,
											ServerCallHandler<R, P> next) {
		Context newCtx = copyMetadataToMetadataContext(metadata);
		return Contexts.interceptCall(newCtx, serverCall, metadata, next);
	}

	private Context copyMetadataToMetadataContext(Metadata headers) {
		MetadataContext metadataContext = METADATA_CONTEXT_KEY.get();
		metadataContext.reset();

		Set<String> keys = headers.keys();

		for (String key : keys) {
			String val = headers.get(Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
			metadataContext.putHeaderFragment(key, val);
		}

		return Context.current().withValue(METADATA_CONTEXT_KEY, metadataContext);
	}

}
