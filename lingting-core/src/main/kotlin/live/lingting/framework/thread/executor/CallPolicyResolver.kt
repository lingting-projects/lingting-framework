package live.lingting.framework.thread.executor

/**
 * @author lingting 2024/12/20 0:02
 */
fun interface CallPolicyResolver {

    /**
     * 不处理该类型任务则返回null
     */
    fun getPolicy(command: Runnable): CallPolicy?

}
