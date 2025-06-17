package live.lingting.framework.grpc

import io.grpc.BindableService
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptor
import java.util.concurrent.TimeUnit
import java.util.function.IntFunction
import java.util.function.UnaryOperator
import live.lingting.framework.Sequence
import live.lingting.framework.grpc.customizer.ServerCustomizer
import live.lingting.framework.grpc.interceptor.GrpcServerCompressionInterceptor
import live.lingting.framework.grpc.properties.GrpcServerProperties

/**
 * @author lingting 2024-01-30 15:37
 */
open class GrpcServerBuilder @JvmOverloads constructor(
    customizers: Collection<ServerCustomizer> = emptyList(),
) {

    protected var services = ArrayList<BindableService>()

    protected var interceptors = ArrayList<ServerInterceptor>()

    protected var port: Int? = null

    protected var properties: GrpcServerProperties? = null

    protected val customizers = customizers.toMutableList()

    fun port(port: Int?): GrpcServerBuilder {
        require(!(port == null || port < 0 || port > 65535)) { "Port [$port] is invalid!" }
        this.port = port
        return this
    }

    fun service(interceptor: BindableService): GrpcServerBuilder {
        services.add(interceptor)
        return this
    }

    fun service(collection: Collection<BindableService>): GrpcServerBuilder {
        services.addAll(collection)
        return this
    }

    fun interceptor(interceptor: ServerInterceptor): GrpcServerBuilder {
        interceptors.add(interceptor)
        return this
    }

    fun interceptor(collection: Collection<ServerInterceptor>): GrpcServerBuilder {
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

    fun customizer(customizer: ServerCustomizer): GrpcServerBuilder {
        customizers.add(customizer)
        return this
    }

    fun customizer(collection: Collection<ServerCustomizer>): GrpcServerBuilder {
        customizers.addAll(collection)
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
        val interceptorsCopy = interceptors.toMutableList()
        properties?.apply {
            builder
                // 单个消息最大大小
                .maxInboundMessageSize(messageSize.toInt())
                .keepAliveTime(keepAliveTime.toMillis(), TimeUnit.MILLISECONDS)
                .keepAliveTimeout(keepAliveTimeout.toMillis(), TimeUnit.MILLISECONDS)

            if (useGzip) {
                val interceptor = GrpcServerCompressionInterceptor.create("gzip")
                interceptorsCopy.add(interceptor)
            }

        }

        val asc = Sequence.asc(customizers.toList())
        asc.forEach {
            val r = it.customize(builder)
            interceptorsCopy.addAll(r)
        }

        return GrpcServer(builder, interceptorsCopy, services)
    }

}
