package live.lingting.polaris.grpc.loadbalance

import com.tencent.polaris.client.api.SDKContext
import io.grpc.LoadBalancer
import io.grpc.LoadBalancerProvider
import io.grpc.LoadBalancerRegistry

/**
 * Provider of [LoadBalancer]s. Each provider is bounded to a load-balancing policy
 * name.
 *
 *
 *
 * Implementations can be automatically discovered by gRPC via Java's SPI mechanism. For
 * automatic discovery, the implementation must have a zero-argument constructor and
 * include a resource named `META-INF/services/io.grpc.LoadBalancerProvider` in
 * their JAR. The file's contents should be the implementation's class name.
 * Implementations that need arguments in their constructor can be manually registered by
 * [LoadBalancerRegistry.register].
 *
 *
 *
 * Implementations *should not* throw. If they do, it may interrupt class loading.
 * If exceptions may reasonably occur for implementation-specific reasons, implementations
 * should generally handle the exception gracefully and return `false` from
 * [.isAvailable].
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisLoadBalancerProvider(context: SDKContext?) : LoadBalancerProvider() {
    private val context = context!!

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPriority(): Int {
        return 0
    }

    override fun getPolicyName(): String {
        return LOADBALANCER_PROVIDER
    }

    override fun newLoadBalancer(helper: LoadBalancer.Helper): LoadBalancer {
        return PolarisLoadBalancer(context, helper)
    }

    companion object {
        const val LOADBALANCER_PROVIDER: String = "polaris"
    }
}
