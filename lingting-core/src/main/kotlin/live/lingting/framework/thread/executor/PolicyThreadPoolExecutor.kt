package live.lingting.framework.thread.executor

import java.util.concurrent.ThreadPoolExecutor
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.value.WaitValue

/**
 * @author lingting 2024/12/19 23:24
 */
open class PolicyThreadPoolExecutor(
    val properties: PolicyThreadPoolProperties
) : ThreadPoolExecutor(
    properties.corePoolSize,
    properties.maximumPoolSize,
    properties.keepAliveTime,
    properties.unit,
    properties.workQueue,
    properties.threadFactory,
    properties.handler
) {
    val log = logger()

    private val resolvers = WaitValue<MutableList<CallPolicyResolver>>()

    fun register(resolver: CallPolicyResolver) {
        resolvers.compute {
            val list = it ?: mutableListOf()
            if (!list.contains(resolver)) {
                list.add(resolver)
            }
            list
        }
    }

    fun unregister(resolver: CallPolicyResolver) {
        resolvers.compute {
            val list = it ?: mutableListOf()
            list.remove(resolver)
            list
        }
    }

    open fun policy(command: Runnable): CallPolicy {
        val value = resolvers.value
        if (!value.isNullOrEmpty()) {
            for (resolver in value) {
                val policy = resolver.getPolicy(command)
                if (policy != null) {
                    log.debug("Hit resolver: {}; Policy: {}", resolver.javaClass.name, policy)
                    return policy
                }
            }
        }
        return CallPolicy.DEFAULT
    }


    override fun execute(command: Runnable) {
        val policy = policy(command)

        if (policy == CallPolicy.DIRECT) {
            command.run()
            return
        }

        super.execute(command)
    }

}
