package live.lingting.framework.thread.executor

/**
 * @author lingting 2024/12/20 0:02
 */
interface ThreadExecuteResolver {

    fun isSupport(command: Runnable): Boolean

    /**
     * 不处理该类型任务则返回null
     */
    fun policy(command: Runnable): CallPolicy? = null

    /**
     * 包装执行器, 不处理返回null
     */
    fun wrapper(command: Runnable): Runnable? = null

}
