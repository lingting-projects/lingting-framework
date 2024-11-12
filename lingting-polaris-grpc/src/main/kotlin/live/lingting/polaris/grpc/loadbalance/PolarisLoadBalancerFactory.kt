package live.lingting.polaris.grpc.loadbalance

import com.tencent.polaris.client.api.SDKContext
import io.grpc.LoadBalancerRegistry

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
object PolarisLoadBalancerFactory {
    fun init(context: SDKContext?) {
        LoadBalancerRegistry.getDefaultRegistry().register(PolarisLoadBalancerProvider(context))
    }
}
