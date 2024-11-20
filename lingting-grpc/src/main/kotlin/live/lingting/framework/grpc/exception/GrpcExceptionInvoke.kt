package live.lingting.framework.grpc.exception

import io.grpc.Attributes
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.SecurityLevel
import io.grpc.ServerCall
import java.lang.reflect.Method

/**
 * @author lingting 2024-03-27 09:40
 */
open class GrpcExceptionInvoke(
    private val instance: GrpcExceptionInstance?,
    private val method: Method?,
    private val handler: GrpcExceptionHandler?
) {
    protected open fun args(e: Exception, call: ServerCall<*, *>, metadata: Metadata?): Array<Any?> {
        val count = method?.parameterCount
        if (count == null || count < 1) {
            return arrayOfNulls(0)
        }
        val args = arrayOfNulls<Any>(count)
        val parameters = method.parameters

        for (i in 0 until count) {
            val parameter = parameters[i]
            val type = parameter.type
            if (type.isAssignableFrom(e.javaClass)) {
                args[i] = e
            } else if (type.isAssignableFrom(Metadata::class.java)) {
                args[i] = metadata
            } else if (type.isAssignableFrom(Attributes::class.java)) {
                args[i] = call.attributes
            } else if (type.isAssignableFrom(SecurityLevel::class.java)) {
                args[i] = call.securityLevel
            } else if (type.isAssignableFrom(MethodDescriptor::class.java)) {
                args[i] = call.methodDescriptor
            } else if (type.isAssignableFrom(ServerCall::class.java)) {
                args[i] = call
            }
        }

        return args
    }

    open fun isSupport(cls: Class<*>): Boolean {
        if (handler == null) {
            return false
        }
        for (a in handler.value) {
            if (a.java.isAssignableFrom(cls)) {
                return true
            }
        }
        return false
    }


    open fun invoke(e: Exception, call: ServerCall<*, *>, metadata: Metadata): Any? {
        val args = args(e, call, metadata)
        return method!!.invoke(instance, *args)
    }
}
