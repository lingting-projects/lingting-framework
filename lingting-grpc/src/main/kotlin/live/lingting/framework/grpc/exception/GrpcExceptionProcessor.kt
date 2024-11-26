package live.lingting.framework.grpc.exception

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.Status
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.Sequence
import live.lingting.framework.kt.logger
import live.lingting.framework.util.AnnotationUtils
import live.lingting.framework.util.ArrayUtils
import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2024-03-27 09:39
 */
open class GrpcExceptionProcessor(instances: Collection<GrpcExceptionInstance>) {

    companion object {
        @JvmField
        val DEFAULT: GrpcExceptionInvoke = GrpcExceptionThrowInvoke()

        private val CACHE: MutableMap<Class<*>, GrpcExceptionInvoke> = ConcurrentHashMap()

        private val log = logger()
    }

    protected val instances = Sequence.asc(instances)

    protected val invokes: MutableList<GrpcExceptionInvoke> = ArrayList()

    init {
        for (instance in this.instances) {
            for (method in ClassUtils.methods(instance.javaClass)) {
                val handler = AnnotationUtils.findAnnotation(method, GrpcExceptionHandler::class.java)
                if (handler != null && !ArrayUtils.isEmpty(handler.value)) {
                    invokes.add(GrpcExceptionInvoke(instance, method, handler))
                }
            }
        }
        CACHE.clear()
    }

    fun find(e: Exception): GrpcExceptionInvoke {
        return CACHE.computeIfAbsent(e.javaClass) {
            for (invoke in invokes) {
                if (invoke.isSupport(it)) {
                    return@computeIfAbsent invoke
                }
            }
            DEFAULT
        }
    }

    class GrpcExceptionThrowInvoke : GrpcExceptionInvoke(null, null, null) {
        override fun isSupport(cls: Class<*>): Boolean {
            return true
        }

        override fun invoke(e: Exception, call: ServerCall<*, *>, metadata: Metadata): Any {
            log!!.error("unknown exception. target: {}", call.methodDescriptor.fullMethodName, e)
            return Status.ABORTED.withCause(e)
        }
    }

}
