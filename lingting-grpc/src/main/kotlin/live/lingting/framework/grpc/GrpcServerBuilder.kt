package live.lingting.framework.grpc

import io.grpc.BindableService
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor
import java.util.concurrent.TimeUnit
import java.util.function.IntFunction
import java.util.function.UnaryOperator
import live.lingting.framework.grpc.properties.GrpcServerProperties

/**
 * @author lingting 2024-01-30 15:37
 */
class GrpcServerBuilder {
    protected var services: MutableList<BindableService> = ArrayList()

    protected var interceptors: MutableList<ServerInterceptor> = ArrayList()

    protected var port: Int? = null

    protected var properties: GrpcServerProperties? = null

    fun port(port: Int?): GrpcServerBuilder {
        require(!(port == null || port < 0 || port > 65535)) { "Port [$port] is invalid!" }
        this.port = port
        return this
    }

    fun service(interceptor: BindableService): GrpcServerBuilder {
        services.add(interceptor)
        return this
    }

    fun service(collection: MutableCollection<BindableService>): GrpcServerBuilder {
        services.addAll(collection)
        return this
    }

    fun interceptor(interceptor: ServerInterceptor): GrpcServerBuilder {
        interceptors.add(interceptor)
        return this
    }

    fun interceptor(collection: MutableCollection<ServerInterceptor>): GrpcServerBuilder {
        interceptors.addAll(collection)
        return this
    }

    fun properties(properties: GrpcServerProperties): GrpcServerBuilder {
        this.properties = properties
        if (properties.port != null) {
            port(properties.port)
        }
        return this
    }

    @JvmOverloads
    fun build(operator: UnaryOperator<ServerBuilder<*>> = UnaryOperator { builder: ServerBuilder<*> -> builder }): GrpcServer {
        return build(IntFunction { forPort ->
            val builder = ServerBuilder.forPort(forPort)
            operator.apply(builder)
        })
    }

    fun build(function: IntFunction<ServerBuilder<*>>): GrpcServer {
        port(port)
        val builder = function.apply(port!!)
        properties?.also {
            // 单个消息最大大小
            builder.maxInboundMessageSize(it.messageSize.toInt())
                .keepAliveTime(it.keepAliveTime.toMillis(), TimeUnit.MILLISECONDS)
                .keepAliveTimeout(it.keepAliveTimeout.toMillis(), TimeUnit.MILLISECONDS)
        }
        return GrpcServer(builder, interceptors, services)
    }
}
