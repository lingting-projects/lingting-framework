
package live.lingting.polaris.grpc.resolver;

import com.tencent.polaris.api.rpc.InstancesResponse;

public interface ResolverInterceptor {

	void before(ResolverContext context);

	InstancesResponse after(ResolverContext context, InstancesResponse response);

	int priority();

}
