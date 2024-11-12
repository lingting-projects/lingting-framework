package live.lingting.framework.interceptor

import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.thread.StackThreadLocal

/**
 * @author lingting 2023-12-18 16:39
 */
class SecurityGrpcRemoteContent private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val THREAD_LOCAL: StackThreadLocal<SecurityToken?> = StackThreadLocal()

        fun get(): SecurityToken? {
            return THREAD_LOCAL.get()
        }

        @JvmStatic
        fun put(value: SecurityToken?) {
            THREAD_LOCAL.put(value)
        }

        @JvmStatic
        fun pop() {
            THREAD_LOCAL.pop()
        }
    }
}
