package live.lingting.polaris.grpc.resolver

import com.tencent.polaris.client.api.SDKContext
import io.grpc.NameResolverRegistry

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
object PolarisNameResolverFactory {
    fun init(context: SDKContext?) {
        NameResolverRegistry.getDefaultRegistry().register(PolarisNameResolverProvider(context!!))
    }
}
