package live.lingting.framework.thread.virtual

import live.lingting.framework.thread.executor.StateKeepExecutorService
import live.lingting.framework.thread.platform.PlatformThread
import live.lingting.framework.util.ClassUtils
import java.util.concurrent.Executors

/**
 * @author lingting 2025/5/12 10:11
 */
class VirtualThreadExecutorService : StateKeepExecutorService {

    companion object {

        @JvmStatic
        val isSupport = ClassUtils.method(Thread::class.java, "ofVirtual") != null

    }

    constructor() : super(
        if (isSupport) {
            Executors.newVirtualThreadPerTaskExecutor()
        } else {
            PlatformThread.delegator
        }
    )

}
