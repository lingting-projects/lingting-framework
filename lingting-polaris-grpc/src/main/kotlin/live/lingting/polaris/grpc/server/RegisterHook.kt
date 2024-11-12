package live.lingting.polaris.grpc.server

import com.tencent.polaris.api.rpc.InstanceRegisterRequest
import com.tencent.polaris.api.rpc.InstanceRegisterResponse

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
interface RegisterHook {
    fun beforeRegister(instance: InstanceRegisterRequest?)

    fun afterRegister(instance: InstanceRegisterResponse?)
}
