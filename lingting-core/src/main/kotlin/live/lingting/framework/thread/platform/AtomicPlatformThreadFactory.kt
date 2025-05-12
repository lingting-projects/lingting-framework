package live.lingting.framework.thread.platform

import live.lingting.framework.thread.AtomicThreadFactory

/**
 * @author lingting 2025/5/12 10:33
 */
class AtomicPlatformThreadFactory(val prefix: String) : AtomicThreadFactory() {

    override fun newThread(index: Long, r: Runnable): Thread? {
        return Thread.ofPlatform()
            .name("$prefix$index")
            .unstarted(r)
    }

}
