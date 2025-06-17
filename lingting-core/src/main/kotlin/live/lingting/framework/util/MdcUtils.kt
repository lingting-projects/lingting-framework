package live.lingting.framework.util

import java.util.UUID
import live.lingting.framework.function.ThrowableRunnable
import org.slf4j.MDC

/**
 * @author lingting 2022/12/11 20:14
 */
object MdcUtils {

    @JvmStatic
    val traceIdKey: String = "traceId"

    @JvmStatic
    fun traceId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    @JvmStatic
    val traceId: String?
        get() = MDC.get(traceIdKey)

    @JvmStatic
    @JvmOverloads
    fun setTraceId(traceId: String = traceId()): String {
        MDC.put(traceIdKey, traceId)
        return traceId
    }

    @JvmStatic
    fun ifAbsentTraceId(): String {
        return traceId ?: setTraceId()
    }

    @JvmStatic
    fun removeTraceId() {
        MDC.remove(traceIdKey)
    }

    @JvmStatic
    fun copyContext(): Map<String, String> {
        return MDC.getCopyOfContextMap() ?: HashMap()
    }

    @JvmStatic
    fun setContext(map: Map<String, String>) {
        MDC.setContextMap(map)
    }

    @JvmStatic
    @JvmOverloads
    fun useTraceId(traceId: String = MdcUtils.traceId ?: traceId(), runnable: ThrowableRunnable) {
        useContext {
            setTraceId(traceId)
            runnable.run()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun useContext(map: Map<String, String> = HashMap(), runnable: ThrowableRunnable) {
        copyContext().let {
            setContext(map)
            try {
                runnable.run()
            } finally {
                setContext(it)
            }
        }
    }

}

