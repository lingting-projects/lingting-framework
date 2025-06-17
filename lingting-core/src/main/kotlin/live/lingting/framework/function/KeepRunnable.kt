package live.lingting.framework.function

import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils

/**
 * 保留状态的可运行代码
 * @author lingting 2024-04-28 17:25
 */
abstract class KeepRunnable @JvmOverloads constructor(
    name: String? = null,
    mdc: Map<String, String>? = null
) : Runnable {

    protected open val log = logger()

    var threadName: String = name ?: ""

    var threadOldName = ""
        private set

    val mdc: Map<String, String> = mdc ?: MdcUtils.copyContext()

    override fun run() {
        val thread = Thread.currentThread()
        threadOldName = thread.name
        if (StringUtils.hasText(threadName)) {
            thread.name = threadName
        }

        MdcUtils.useContext(mdc) {
            onMdc()
        }
    }

    private fun onMdc() {
        val thread = Thread.currentThread()
        try {
            onStart()
            doProcess()
        } catch (_: InterruptedException) {
            thread.interrupt()
            log.warn("Thread interrupted inside keep runnable")
        } catch (throwable: Throwable) {
            log.error("Thread exception inside keep runnable!", throwable)
        } finally {
            onFinally()
            thread.name = threadOldName
        }
    }

    protected open fun onStart() {
        //
    }

    protected abstract fun doProcess()

    protected open fun onFinally() {
        //
    }


}
