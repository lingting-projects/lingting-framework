package live.lingting.framework.security.grpc.interceptor

import live.lingting.framework.context.StackContext
import live.lingting.framework.security.domain.SecurityToken

/**
 * @author lingting 2023-12-18 16:39
 */
object SecurityGrpcRemoteContent {
    val THREAD_LOCAL: StackContext<SecurityToken?> = StackContext()

    @JvmStatic
    fun get(): SecurityToken? {
        return THREAD_LOCAL.peek()
    }

    @JvmStatic
    fun put(value: SecurityToken?) {
        THREAD_LOCAL.push(value)
    }

    @JvmStatic
    fun pop() {
        THREAD_LOCAL.pop()
    }

}
