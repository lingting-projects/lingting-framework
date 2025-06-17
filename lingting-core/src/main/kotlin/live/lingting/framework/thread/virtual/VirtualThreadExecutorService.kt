package live.lingting.framework.thread.virtual

import live.lingting.framework.thread.executor.StateKeepExecutorService
import live.lingting.framework.thread.platform.PlatformThread

/**
 * @author lingting 2025/5/12 10:11
 */
class VirtualThreadExecutorService : StateKeepExecutorService {

    companion object {

        @JvmStatic
        val isSupport = false

    }

    constructor() : super(PlatformThread.delegator)

}
