
package live.lingting.polaris.grpc.loadbalance;

import com.tencent.polaris.client.api.SDKContext;
import io.grpc.LoadBalancerRegistry;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class PolarisLoadBalancerFactory {

	private PolarisLoadBalancerFactory() {
	}

	public static void init(final SDKContext context) {
		LoadBalancerRegistry.getDefaultRegistry().register(new PolarisLoadBalancerProvider(context));
	}

}
