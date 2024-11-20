
package live.lingting.polaris.grpc.interceptor;

import com.tencent.polaris.client.api.SDKContext;
import io.grpc.ClientInterceptor;

/**
 * client 侧的拦截器, 优先级优于 ClientInterceptor
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public abstract class PolarisClientInterceptor implements ClientInterceptor {

	/**
	 * client 侧的拦截器，自动注入当前grpc-server与polaris有关的信息
	 *
	 * @param namespace       当前主调服务所在的命名空间
	 * @param applicationName 当前主调服务的应用名称
	 * @param context         polaris-sdk的上下文信息，整个服务调用者进程一个
	 */
	public abstract void init(final String namespace, final String applicationName, final SDKContext context);

}
