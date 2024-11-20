package live.lingting.framework.grpc

import io.grpc.Channel
import io.grpc.ClientInterceptor
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.netty.GrpcSslContexts
import io.grpc.netty.NettyChannelBuilder
import io.grpc.stub.AbstractAsyncStub
import io.grpc.stub.AbstractBlockingStub
import io.grpc.stub.AbstractFutureStub
import io.grpc.stub.AbstractStub
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.function.UnaryOperator
import live.lingting.framework.Sequence
import live.lingting.framework.grpc.properties.GrpcClientProperties
import live.lingting.framework.util.CollectionUtils

/**
 * @author lingting 2023-12-15 14:44
 */
class GrpcClientProvide(val properties: GrpcClientProperties, val interceptors: MutableList<ClientInterceptor>) {
    // region builder
    /**
     * 直接使用默认配置构建
     */
    fun builder(): GrpcChannelBuilder {
        return builder(properties.host, properties.port).provide()
    }

    fun builder(target: String): GrpcChannelBuilder {
        val builder = GrpcChannelBuilder(this)
        return builder.target(target).provide()
    }

    /**
     * 使用自定义配置构建, 不会自动加入配置
     */
    fun builder(host: String?, port: Int?): GrpcChannelBuilder {
        val builder = GrpcChannelBuilder(this)
        return builder.address(host, port)
    }

    @JvmOverloads
    fun addInterceptors(builder: ManagedChannelBuilder<*>, collection: MutableCollection<ClientInterceptor> = interceptors) {
        if (CollectionUtils.isEmpty(collection)) {
            return
        }

        // 升序排序
        val asc = Sequence.asc(collection)
        // 注册拦截器
        builder.intercept(asc)
    }

    @JvmOverloads
    fun useProperties(builder: ManagedChannelBuilder<*>, properties: GrpcClientProperties = this.properties) {
        // 开启心跳
        if (properties.isEnableKeepAlive) {
            builder.keepAliveTime(properties.keepAliveTime.toMillis(), TimeUnit.MILLISECONDS)
                .keepAliveTimeout(properties.keepAliveTimeout.toMillis(), TimeUnit.MILLISECONDS)
        }

        // 使用明文
        if (properties.isUsePlaintext) {
            builder.usePlaintext()
        }

        // 重试
        if (properties.isEnableRetry) {
            builder.enableRetry()
        }

        // ssl配置
        if (!properties.isUsePlaintext && properties.isDisableSsl
            && builder is NettyChannelBuilder
        ) {
            val sslContextBuilder = GrpcSslContexts.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
            builder.sslContext(sslContextBuilder.build())
        }
    }


    // endregion
    fun channel(): ManagedChannel {
        return builder().build()
    }

    @JvmOverloads
    fun channel(host: String, port: Int, operator: UnaryOperator<ManagedChannelBuilder<*>> = UnaryOperator { builder: ManagedChannelBuilder<*> -> builder }): ManagedChannel {
        return builder(host, port).provide().build(operator)
    }

    @JvmOverloads
    fun channel(builder: ManagedChannelBuilder<*>, list: MutableList<ClientInterceptor> = interceptors): ManagedChannel {
        useProperties(builder)
        addInterceptors(builder, list)
        return builder.build()
    }

    fun <R : AbstractStub<R>> stub(channel: Channel, function: Function<Channel, R>): R {
        return function.apply(channel)
    }

    fun <R : AbstractAsyncStub<R>> async(channel: Channel, function: Function<Channel, R>): R {
        return stub<R>(channel, function)
    }

    fun <R : AbstractBlockingStub<R>> blocking(channel: Channel, function: Function<Channel, R>): R {
        return stub<R>(channel, function)
    }

    fun <R : AbstractFutureStub<R>> future(channel: Channel, function: Function<Channel, R>): R {
        return stub<R>(channel, function)
    }
}
