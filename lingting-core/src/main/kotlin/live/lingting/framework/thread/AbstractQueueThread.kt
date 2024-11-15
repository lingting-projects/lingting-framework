package live.lingting.framework.thread

import java.time.Duration
import live.lingting.framework.time.StopWatch
import live.lingting.framework.util.CollectionUtils


/**
 * 顶级队列线程类
 *
 * @author lingting 2021/3/2 15:07
 */
abstract class AbstractQueueThread<E> : AbstractThreadContextComponent() {

    /**
     * 用于子类自定义缓存数据数量
     * @return long
     */
    open val batchSize: Int = BATCH_SIZE

    /**
     * 用于子类自定义等待时长
     *
     *
     * 不要和 [.getPollTimeout]值相差过大, 否则会导致等待时间不是预期的值, 而是
     * [.getPollTimeout]的值
     *
     * @return 返回时长，单位毫秒
     */
    open val batchTimeout: Duration = BATCH_TIMEOUT

    /**
     * 用于子类自定义 获取数据的超时时间
     * @return 返回时长，单位毫秒
     */
    open val pollTimeout: Duration = POLL_TIMEOUT

    protected val data: MutableList<E> = ArrayList<E>(batchSize)

    /**
     * 往队列插入数据
     * @param e 数据
     */
    abstract fun put(e: E?)

    /**
     * 数据处理前执行
     */
    protected fun preProcess() {
    }

    /**
     * 从队列中取值
     * @param timeout 等待时长
     * @return E
     * @throws InterruptedException 线程中断
     */
    protected abstract fun poll(timeout: Duration): E?

    /**
     * 处理单个接收的数据
     * @param e 接收的数据
     * @return 返回要放入队列的数据
     */
    protected fun process(e: E): E {
        return e
    }

    /**
     * 处理所有已接收的数据
     * @param list 所有已接收的数据
     * @throws Exception 异常
     */
    protected abstract fun process(list: List<E>?)

    override fun doRun() {
        preProcess()
        fill()
        if (!CollectionUtils.isEmpty(data)) {
            process(ArrayList(data))
            data.clear()
        }
    }

    /**
     * 填充数据
     */
    protected fun fill() {
        var count = 0
        val watch = StopWatch()
        while (count < this.batchSize) {
            val e = poll()
            val p = if (e != null) process(e) else null

            if (p != null) {
                // 第一次插入数据
                if (count++ == 0) {
                    // 记录时间
                    watch.restart()
                }
                // 数据存入列表
                data.add(p)
            }

            // 需要进行处理数据了
            if (isBreak(watch)) {
                break
            }
        }
    }

    /**
     * 是否中断数据填充
     */
    protected fun isBreak(watch: StopWatch): Boolean {
        // 该停止运行了
        if (!isRun) {
            return true
        }

        // 已有数据且超过设定的等待时间
        return isTimeout(watch)
    }

    /**
     * 已有数据且超过设定的等待时间
     */
    protected fun isTimeout(watch: StopWatch): Boolean {
        return !CollectionUtils.isEmpty(data) && watch.timeMillis() >= this.batchTimeout.toMillis()
    }

    fun poll(): E? {
        var e: E? = null
        try {
            val duration: Duration = this.pollTimeout
            e = poll(duration)
        } catch (ex: InterruptedException) {
            log.error("Class: {}; ThreadId: {}; poll interrupted!", simpleName, threadId())
            interrupt()
        }
        return e
    }

    /**
     * 线程被中断后的处理. 如果有缓存手段可以让数据进入缓存.
     */
    override fun shutdown() {
        log.warn("Class: {}; ThreadId: {}; shutdown! data: {}", simpleName, threadId(), data)
    }

    companion object {
        /**
         * 默认缓存数据数量
         */
        @JvmStatic
        val BATCH_SIZE: Int = 500

        /**
         * 默认等待时长 30秒
         */
        @JvmStatic
        val BATCH_TIMEOUT: Duration = Duration.ofSeconds(30)

        /**
         * 默认获取数据时的超时时间
         */
        @JvmStatic
        val POLL_TIMEOUT: Duration = Duration.ofSeconds(5)
    }
}
