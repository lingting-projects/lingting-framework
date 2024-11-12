
package live.lingting.polaris.grpc.loadbalance;

import com.tencent.polaris.client.api.SDKContext;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancerProvider;
import io.grpc.LoadBalancerRegistry;

/**
 * Provider of {@link LoadBalancer}s. Each provider is bounded to a load-balancing policy
 * name.
 *
 * <p>
 * Implementations can be automatically discovered by gRPC via Java's SPI mechanism. For
 * automatic discovery, the implementation must have a zero-argument constructor and
 * include a resource named {@code META-INF/services/io.grpc.LoadBalancerProvider} in
 * their JAR. The file's contents should be the implementation's class name.
 * Implementations that need arguments in their constructor can be manually registered by
 * {@link LoadBalancerRegistry#register}.
 *
 * <p>
 * Implementations <em>should not</em> throw. If they do, it may interrupt class loading.
 * If exceptions may reasonably occur for implementation-specific reasons, implementations
 * should generally handle the exception gracefully and return {@code false} from
 * {@link #isAvailable()}.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public class PolarisLoadBalancerProvider extends LoadBalancerProvider {

	public static final String LOADBALANCER_PROVIDER = "polaris";

	private final SDKContext context;

	public PolarisLoadBalancerProvider(SDKContext context) {
		this.context = context;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public String getPolicyName() {
		return LOADBALANCER_PROVIDER;
	}

	@Override
	public LoadBalancer newLoadBalancer(Helper helper) {
		return new PolarisLoadBalancer(context, helper);
	}

}
