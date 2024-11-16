package live.lingting.framework.util

import java.util.UUID
import org.slf4j.MDC


/**
 * @author lingting 2022/12/11 20:14
 */
object MdcUtils {

    const val TRACE_ID: String = "traceId"

    @JvmStatic
    fun traceId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    @JvmStatic
    val traceId: String?
        get() = MDC.get(TRACE_ID)

    @JvmStatic
    fun fillTraceId(): String {
        val traceId = traceId()
        fillTraceId(traceId)
        return traceId
    }

    @JvmStatic
    fun fillTraceId(traceId: String) {
        MDC.put(TRACE_ID, traceId)
    }

    @JvmStatic
    fun removeTraceId() {
        MDC.remove(TRACE_ID)
    }

    @JvmStatic
    fun copyContext(): Map<String, String> {
        return MDC.getCopyOfContextMap() ?: HashMap()
    }
}

