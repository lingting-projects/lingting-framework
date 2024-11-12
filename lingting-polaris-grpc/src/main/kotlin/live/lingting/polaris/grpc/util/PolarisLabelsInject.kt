package live.lingting.polaris.grpc.util

import com.tencent.polaris.api.pojo.RouteArgument
import com.tencent.polaris.ratelimit.api.rpc.Argument

/**
 * PolarisLabelsInject 针对每次流量的用户自定义标签注入
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
interface PolarisLabelsInject {
    /**
     * 注入本次流量的路由标签信息
     * @param arguments [<]
     * @return [<]
     */
    fun modifyRoute(arguments: Set<RouteArgument>): Set<RouteArgument>

    /**
     * 注入本次流量的限流标签信息
     * @param arguments [<]
     * @return [<]
     */
    fun modifyRateLimit(arguments: Set<Argument>): Set<Argument>
}
