package live.lingting.polaris.grpc.resolver

import com.tencent.polaris.api.core.ConsumerAPI
import com.tencent.polaris.client.api.SDKContext
import com.tencent.polaris.factory.api.DiscoveryAPIFactory
import io.grpc.NameResolver
import io.grpc.NameResolverProvider
import java.net.URI

/**
 * Service provider class
 *
 * @author lixiaoshuang
 */
class PolarisNameResolverProvider(private val context: SDKContext) : NameResolverProvider() {
    private val consumerAPI: ConsumerAPI = DiscoveryAPIFactory.createConsumerAPIByContext(context)

    /**
     * Creates a NameResolver for the given target URI.
     * @param targetUri the target URI to be resolved, whose scheme must not be null
     * @param args other information that may be useful
     * @return NameResolver
     */
    override fun newNameResolver(targetUri: URI, args: NameResolver.Args): NameResolver? {
        if (DEFAULT_SCHEME != targetUri.scheme) {
            return null
        }
        return PolarisNameResolver(targetUri, context, consumerAPI)
    }

    /**
     * service is available.
     * @return isAvailable
     */
    override fun isAvailable(): Boolean {
        return true
    }

    /**
     * Default priority 5.
     * @return priority
     */
    override fun priority(): Int {
        return DEFAULT_PRIORITY
    }

    override fun getDefaultScheme(): String {
        return DEFAULT_SCHEME
    }

    companion object {
        private const val DEFAULT_PRIORITY = 5

        private const val DEFAULT_SCHEME = "polaris"
    }
}
