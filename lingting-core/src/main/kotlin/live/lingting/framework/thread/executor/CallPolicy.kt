package live.lingting.framework.thread.executor

/**
 * @author lingting 2024/12/19 23:23
 */
enum class CallPolicy() {

    /**
     * 使用新线程执行
     */
    ASYNC,

    /**
     * 使用当前线程执行
     */
    DIRECT,

    ;

    companion object {

        @JvmField
        val DEFAULT = ASYNC

    }

}
