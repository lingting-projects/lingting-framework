
package live.lingting.polaris.grpc.server;

import com.tencent.polaris.api.rpc.InstanceRegisterRequest;
import com.tencent.polaris.api.rpc.InstanceRegisterResponse;

/**
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public interface RegisterHook {

	void beforeRegister(InstanceRegisterRequest instance);

	void afterRegister(InstanceRegisterResponse instance);

}
