
package live.lingting.polaris.grpc.resolver;

import com.tencent.polaris.client.api.SDKContext;
import io.grpc.NameResolverRegistry;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class PolarisNameResolverFactory {

	private PolarisNameResolverFactory() {
	}

	public static void init(final SDKContext context) {
		NameResolverRegistry.getDefaultRegistry().register(new PolarisNameResolverProvider(context));
	}

}
