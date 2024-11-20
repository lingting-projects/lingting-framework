package live.lingting.framework.grpc

import io.grpc.BindableService
import io.grpc.MethodDescriptor
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor
import io.grpc.ServiceDescriptor
import java.lang.reflect.Method
import live.lingting.framework.Sequence
import live.lingting.framework.context.ContextComponent
import live.lingting.framework.grpc.interceptor.AbstractServerInterceptor
import live.lingting.framework.util.ClassUtils
import live.lingting.framework.util.ThreadUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2023-04-14 17:38
 */
@Suppress("UNCHECKED_CAST")
class GrpcServer(
    builder: ServerBuilder<*>, interceptors: MutableCollection<ServerInterceptor>,
    services: MutableCollection<BindableService>
) : ContextComponent {
    val server: Server

    val serviceNameMap: MutableMap<String, Class<out BindableService>>

    val fullMethodNameMap: MutableMap<String, Method>

    init {
        // 升序排序
        val asc = Sequence.asc(interceptors)
        // 获取一个游标在尾部的迭代器
        val iterator = asc.listIterator(asc.size)
        // 服务端是最后注册的拦截器最先执行, 所以要倒序注册
        while (iterator.hasPrevious()) {
            val previous = iterator.previous()
            if (previous is AbstractServerInterceptor) {
                previous.server = this
            }
            builder.intercept(previous)
        }

        this.serviceNameMap = HashMap()
        this.fullMethodNameMap = HashMap()

        // 注册服务
        for (service in services) {
            builder.addService(service)
            fillMap(service)
        }

        this.server = builder.build()
    }

    /**
     * 填充服务名map和全方法名map
     */
    protected fun fillMap(service: BindableService) {
        val cls: Class<out BindableService> = service.javaClass

        val serverServiceDefinition = service.bindService()
        val serviceDescriptor = serverServiceDefinition.serviceDescriptor

        serviceNameMap[serviceDescriptor.name] = cls

        for (serverMethodDefinition in serverServiceDefinition.methods) {
            val methodDescriptor = serverMethodDefinition.methodDescriptor
            val fullMethodName = methodDescriptor.fullMethodName
            fullMethodNameMap[fullMethodName] = resolve(methodDescriptor, cls)!!
        }
    }

    val isRunning: Boolean
        get() = !server.isShutdown && !server.isTerminated

    fun port(): Int {
        return server.port
    }

    fun findClass(descriptor: ServiceDescriptor): Class<out BindableService>? {
        return serviceNameMap[descriptor.name]
    }

    fun findClass(descriptor: MethodDescriptor<*, *>): Class<out BindableService>? {
        val method = findMethod(descriptor)
        if (method == null) {
            return null
        }
        return method.declaringClass as Class<out BindableService>
    }

    fun findMethod(descriptor: MethodDescriptor<*, *>): Method? {
        return fullMethodNameMap[descriptor.fullMethodName]
    }

    protected fun resolve(descriptor: MethodDescriptor<*, *>, cls: Class<out BindableService>): Method? {
        val bareMethodName = descriptor.bareMethodName

        for (method in ClassUtils.methods(cls)) {
            if (method.name == bareMethodName) {
                return method
            }
        }

        return null
    }

    override fun onApplicationStart() {
        server.start()
        log.info("grpc server started. port: {}", server.port)
        ThreadUtils.execute("GrpcServer", server::awaitTermination)
    }

    override fun onApplicationStop() {
        log.warn("shutdown grpc server!")
        server.shutdown()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(GrpcServer::class.java)
    }
}
