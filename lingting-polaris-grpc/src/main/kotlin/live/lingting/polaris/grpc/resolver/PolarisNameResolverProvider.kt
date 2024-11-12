
package live.lingting.polaris.grpc.resolver;

import com.tencent.polaris.api.core.ConsumerAPI;
import com.tencent.polaris.client.api.SDKContext;
import com.tencent.polaris.factory.api.DiscoveryAPIFactory;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

/**
 * Service provider class
 *
 * @author lixiaoshuang
 */
public class PolarisNameResolverProvider extends NameResolverProvider {

	private static final int DEFAULT_PRIORITY = 5;

	private static final String DEFAULT_SCHEME = "polaris";

	private final SDKContext context;

	private final ConsumerAPI consumerAPI;

	public PolarisNameResolverProvider(final SDKContext context) {
		this.context = context;
		this.consumerAPI = DiscoveryAPIFactory.createConsumerAPIByContext(context);
	}

	/**
	 * Creates a NameResolver for the given target URI.
	 * @param targetUri the target URI to be resolved, whose scheme must not be null
	 * @param args other information that may be useful
	 * @return NameResolver
	 */
	@Override
	public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
		if (!DEFAULT_SCHEME.equals(targetUri.getScheme())) {
			return null;
		}
		return new PolarisNameResolver(targetUri, context, consumerAPI);
	}

	/**
	 * service is available.
	 * @return isAvailable
	 */
	@Override
	protected boolean isAvailable() {
		return true;
	}

	/**
	 * Default priority 5.
	 * @return priority
	 */
	@Override
	protected int priority() {
		return DEFAULT_PRIORITY;
	}

	@Override
	public String getDefaultScheme() {
		return DEFAULT_SCHEME;
	}

}
