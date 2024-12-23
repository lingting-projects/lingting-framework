package live.lingting.framework.thread.executor

import java.util.concurrent.ThreadPoolExecutor
import java.util.function.Function
import live.lingting.framework.thread.WorkerRunnable
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

    private val resolvers = WaitValue<MutableList<ThreadExecuteResolver>>()

    fun register(resolver: ThreadExecuteResolver) {
        resolvers.compute {
            val list = it ?: mutableListOf()
            if (!list.contains(resolver)) {
                list.add(resolver)
            }
            list
        }
    }

    fun unregister(resolver: ThreadExecuteResolver) {
        resolvers.compute {
            val list = it ?: mutableListOf()
            list.remove(resolver)
            list
        }
    }

    open fun <R> invoke(value: Collection<ThreadExecuteResolver>?, func: Function<ThreadExecuteResolver, R?>): R? {
        if (!value.isNullOrEmpty()) {
            for (resolver in value) {
                val r = func.apply(resolver)
                if (r != null) {
                    log.trace("Hit resolver: {};", resolver.javaClass.name)
                    return r
                }
            }
        }
        return null
    }

    override fun execute(command: Runnable) {
        val value = resolvers.value?.filter { it.isSupport(command) }
        val r = invoke(value) { it.wrapper(command) } ?: command

        // 已启动的 WorkerRunnable 忽略, 避免重复启动
        if (r is WorkerRunnable && r.isStarted()) {
            return
        }

        val policy = invoke(value) { it.policy(command) } ?: CallPolicy.DEFAULT
        if (policy == CallPolicy.DIRECT) {
            r.run()
            return
        }

        super.execute(r)
    }

}
