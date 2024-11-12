package live.lingting.framework.util

import org.slf4j.MDC
import java.util.*

/**
 * @author lingting 2022/12/11 20:14
 */
class MdcUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val TRACE_ID: String = "traceId"


        fun traceId(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }


        val traceId: String
            get() = MDC.get(TRACE_ID)


        fun fillTraceId(): String {
            val traceId = traceId()
            fillTraceId(traceId)
            return traceId
        }


        fun fillTraceId(traceId: String?) {
            MDC.put(TRACE_ID, traceId)
        }


        fun removeTraceId() {
            MDC.remove(TRACE_ID)
        }

        fun copyContext(): Map<String, String> {
            val copy = MDC.getCopyOfContextMap() ?: return HashMap()
            return copy
        }
    }
}
