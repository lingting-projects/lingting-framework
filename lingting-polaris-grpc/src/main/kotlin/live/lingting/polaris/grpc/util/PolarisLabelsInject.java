
package live.lingting.polaris.grpc.util;

import com.tencent.polaris.api.pojo.RouteArgument;
import com.tencent.polaris.ratelimit.api.rpc.Argument;

import java.util.Set;

/**
 * PolarisLabelsInject 针对每次流量的用户自定义标签注入
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public interface PolarisLabelsInject {

	/**
	 * 注入本次流量的路由标签信息
	 *
	 * @param arguments {@link Set<RouteArgument>}
	 * @return {@link Set<RouteArgument>}
	 */
	Set<RouteArgument> modifyRoute(Set<RouteArgument> arguments);

	/**
	 * 注入本次流量的限流标签信息
	 *
	 * @param arguments {@link Set<Argument>}
	 * @return {@link Set<Argument>}
	 */
	Set<Argument> modifyRateLimit(Set<Argument> arguments);

}
