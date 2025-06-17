package live.lingting.framework.thread.executor

/**
 * @author lingting 2025/5/13 13:48
 */
enum class ExecutorState(
    val isShutdown: Boolean,
    val isTerminated: Boolean,
) {

    /**
     * 运行中
     */
    RUNNING(false, false),

    /**
     * 关闭
     */
    SHUTDOWN(true, false),

    /**
     * 终止
     */
    TERMINATED(true, true),

    ;

}
