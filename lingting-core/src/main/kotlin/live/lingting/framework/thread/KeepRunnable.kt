package live.lingting.framework.thread

import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils

/**
 * 保留状态的可运行代码
 * @author lingting 2024-04-28 17:25
 */
abstract class KeepRunnable protected constructor(
    protected val name: String,
    protected val mdc: Map<String, String>
) : Runnable {

    constructor() : this("")

    constructor(name: String?) : this(name ?: "", MdcUtils.copyContext())

    protected val log = logger()

    override fun run() {
        val thread = Thread.currentThread()
        val oldName = thread.name
        if (StringUtils.hasText(name)) {
            thread.name = name
        }

        MdcUtils.useContext(mdc) {
            try {
                process()
            } catch (_: InterruptedException) {
                thread.interrupt()
                log.warn("Thread interrupted inside thread pool")
            } catch (throwable: Throwable) {
                log.error("Thread exception inside thread pool!", throwable)
            } finally {
                onFinally()
                thread.name = oldName
            }
        }
    }

    protected abstract fun process()

    protected open fun onFinally() {
        //
    }

}
