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

    protected val log = logger()

    var name: String = name ?: ""

    val mdc: Map<String, String> = mdc ?: MdcUtils.copyContext()

    override fun run() {
        val thread = Thread.currentThread()
        val oldName = thread.name
        if (StringUtils.hasText(name)) {
            thread.name = name
        }

        MdcUtils.useContext(mdc) {
            try {
                onStart()
                doProcess()
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

    protected open fun onStart() {
        //
    }

    protected abstract fun doProcess()

    protected open fun onFinally() {
        //
    }


}
