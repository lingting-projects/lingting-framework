package live.lingting.framework.grpc

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.AbstractAsyncStub
import io.grpc.stub.AbstractBlockingStub
import io.grpc.stub.AbstractFutureStub
import io.grpc.stub.AbstractStub
import java.util.function.Function
import java.util.function.UnaryOperator
import live.lingting.framework.grpc.properties.GrpcClientProperties
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024-01-30 13:55
 */
class GrpcChannelBuilder(protected val provide: GrpcClientProvide) {
    private val interceptors: MutableList<ClientInterceptor> = ArrayList()

    protected var target: String? = null

    protected var properties: GrpcClientProperties? = null

    fun address(host: String?, port: Int?): GrpcChannelBuilder {
        require(StringUtils.hasText(host)) { "Host [$host] is invalid!" }
        require(!(port == null || port < 0 || port > 65535)) { "Port [$port] is invalid!" }
        this.target = "$host:$port"
        return this
    }

    fun target(target: String): GrpcChannelBuilder {
        this.target = target
        return this
    }

    fun interceptor(): GrpcChannelBuilder {
        return interceptor(provide.interceptors)
    }

    fun interceptor(interceptor: ClientInterceptor): GrpcChannelBuilder {
        interceptors.add(interceptor)
        return this
    }

    fun interceptor(collection: MutableCollection<ClientInterceptor>): GrpcChannelBuilder {
        interceptors.addAll(collection)
        return this
    }

    /**
     * 使用 provide的配置填充
     */
    fun provide(): GrpcChannelBuilder {
        return interceptor().properties()
    }

    fun properties(): GrpcChannelBuilder {
        return properties(provide.properties)
    }

    fun properties(properties: GrpcClientProperties): GrpcChannelBuilder {
        this.properties = properties
        return this
    }

    @JvmOverloads
    fun build(operator: UnaryOperator<ManagedChannelBuilder<*>> = UnaryOperator { builder: ManagedChannelBuilder<*> -> builder }): ManagedChannel {
        return build(Function { forTarget ->
            val builder = ManagedChannelBuilder.forTarget(forTarget)
            operator.apply(builder)
        })
    }

    fun build(function: Function<String, ManagedChannelBuilder<*>>): ManagedChannel {
        val builder = function.apply(target!!)
        if (properties != null) {
            provide.useProperties(builder, properties!!)
        }
        provide.addInterceptors(builder, interceptors)
        return builder.build()
    }

    fun <R : AbstractStub<R>> stub(function: Function<ManagedChannel, R>): R {
        val channel = build()
        return function.apply(channel)
    }

    fun <R : AbstractAsyncStub<R>> async(function: Function<ManagedChannel, R>): R {
        return stub<R>(function)
    }

    fun <R : AbstractBlockingStub<R>> blocking(function: Function<ManagedChannel, R>): R {
        return stub<R>(function)
    }

    fun <R : AbstractFutureStub<R>> future(function: Function<ManagedChannel, R>): R {
        return stub<R>(function)
    }
}
