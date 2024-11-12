package live.lingting.framework.grpc.exception

import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.Status
import live.lingting.framework.Sequence
import live.lingting.framework.util.AnnotationUtils
import live.lingting.framework.util.ArrayUtils
import live.lingting.framework.util.ClassUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024-03-27 09:39
 */
class GrpcExceptionProcessor(instances: MutableCollection<GrpcExceptionInstance?>?) {
    private val instances: MutableList<GrpcExceptionInstance?>?

    private val invokes: MutableList<GrpcExceptionInvoke?> = ArrayList()

    init {
        this.instances = Sequence.asc(instances)
        init()
    }

    fun init() {
        for (instance in instances!!) {
            for (method in ClassUtils.methods(instance.javaClass)) {
                val handler = AnnotationUtils.findAnnotation(method!!, GrpcExceptionHandler::class.java)
                if (handler != null && !ArrayUtils.isEmpty(handler.value)) {
                    invokes.add(GrpcExceptionInvoke(instance, method, handler))
                }
            }
        }
        CACHE.clear()
    }

    fun find(e: Exception): GrpcExceptionInvoke {
        return CACHE.computeIfAbsent(e.javaClass) { k: Class<*>? ->
            for (invoke in invokes) {
                if (invoke!!.isSupport(k!!)) {
                    return@computeIfAbsent invoke
                }
            }
            DEFAULT
        }!!
    }

    class GrpcExceptionThrowInvoke : GrpcExceptionInvoke(null, null, null) {
        override fun isSupport(cls: Class<*>?): Boolean {
            return true
        }


        override fun invoke(e: Exception?, call: ServerCall<*, *>, metadata: Metadata?): Any {
            log!!.error("unknown exception. target: {}", call.methodDescriptor.fullMethodName, e)
            return Status.ABORTED.withCause(e)
        }
    }

    companion object {
        private val DEFAULT: GrpcExceptionInvoke = GrpcExceptionThrowInvoke()

        private val CACHE: MutableMap<Class<*>?, GrpcExceptionInvoke?> = ConcurrentHashMap()
        private val log: Logger? = LoggerFactory.getLogger(GrpcExceptionProcessor::class.java)
    }
}
