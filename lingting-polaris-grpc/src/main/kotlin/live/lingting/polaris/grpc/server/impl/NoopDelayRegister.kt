package live.lingting.polaris.grpc.server.impl

import live.lingting.polaris.grpc.server.DelayRegister

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class NoopDelayRegister : DelayRegister {
    override fun allowRegis(): Boolean {
        return true
    }
}
