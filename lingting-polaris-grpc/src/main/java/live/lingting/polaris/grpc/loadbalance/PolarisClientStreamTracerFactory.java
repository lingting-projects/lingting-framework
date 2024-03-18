
package live.lingting.polaris.grpc.loadbalance;

import io.grpc.ClientStreamTracer;
import io.grpc.ClientStreamTracer.Factory;
import io.grpc.ClientStreamTracer.StreamInfo;
import io.grpc.Metadata;
import live.lingting.polaris.grpc.util.ClientCallInfo;

/**
 * Factory class for {@link ClientStreamTracer}.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class PolarisClientStreamTracerFactory extends Factory {

	private final ClientCallInfo callInfo;

	public PolarisClientStreamTracerFactory(final ClientCallInfo callInfo) {
		super();
		this.callInfo = callInfo;
	}

	@Override
	public ClientStreamTracer newClientStreamTracer(StreamInfo info, Metadata headers) {
		return new PolarisClientStreamTracer(info, headers, callInfo);
	}

}
