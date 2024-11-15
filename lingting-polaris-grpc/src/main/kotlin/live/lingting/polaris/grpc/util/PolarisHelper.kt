package live.lingting.polaris.grpc.util

import com.tencent.polaris.api.pojo.RouteArgument
import com.tencent.polaris.ratelimit.api.rpc.Argument
import com.tencent.polaris.ratelimit.api.rpc.QuotaResponse
import io.grpc.ClientInterceptor
import io.grpc.ServerInterceptor
import io.grpc.Status
import live.lingting.polaris.grpc.client.MetadataClientInterceptor
import live.lingting.polaris.grpc.ratelimit.PolarisRateLimitServerInterceptor
import live.lingting.polaris.grpc.server.MetadataServerInterceptor

import java.util.function.BiFunction
import java.util.function.Predicate

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisHelper private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    class PolarisRateLimitInterceptorBuilder {
        private var rateLimitCallback = BiFunction { quotaResponse: QuotaResponse?, method: String? -> Status.UNAVAILABLE.withDescription("rate-limit exceeded (server side)") }

        /**
         * 当限流触发时，用户自定义的限流结果返回器
         *
         * @param rateLimitCallback [,][<]
         * @return [PolarisRateLimitInterceptorBuilder]
         */
        fun rateLimitCallback(
            rateLimitCallback: BiFunction<QuotaResponse, String, Status>
        ): PolarisRateLimitInterceptorBuilder {
            this.rateLimitCallback = rateLimitCallback
            return this
        }

        fun build(): PolarisRateLimitServerInterceptor {
            val polarisRateLimitInterceptor = PolarisRateLimitServerInterceptor()
            polarisRateLimitInterceptor.setRateLimitCallback(this.rateLimitCallback)
            return polarisRateLimitInterceptor
        }
    }

    companion object {
        /**
         * 调用此方法注入用户自定义的 PolarisLabelsInject
         *
         * @param inject [PolarisLabelsInject]
         */
        /**
         * [PolarisLabelsInject] 用户自定义的 PolarisLabelsInject 实现，可以在处理每次流量时，通过
         * [PolarisLabelsInject.modifyRoute]} 或者
         * [PolarisLabelsInject.modifyRateLimit] 注入本次流量的标签信息
         */
        var labelsInject: PolarisLabelsInject

        init {
            val serviceLoader = ServiceLoader.load(PolarisLabelsInject::class.java)
            val iterator: Iterator<PolarisLabelsInject> = serviceLoader.iterator()
            labelsInject = Optional.ofNullable(if (iterator.hasNext()) iterator.next() else null).orElse(object : PolarisLabelsInject {
                override fun modifyRoute(arguments: Set<RouteArgument>): Set<RouteArgument> {
                    return arguments
                }

                override fun modifyRateLimit(arguments: Set<Argument>): Set<Argument> {
                    return arguments
                }
            })
        }

        fun buildMetadataClientInterceptor(): ClientInterceptor {
            return MetadataClientInterceptor { s: String? -> true }
        }

        fun buildMetadataClientInterceptor(predicate: Predicate<String?>): ClientInterceptor {
            return MetadataClientInterceptor(predicate)
        }

        fun buildMetadataServerInterceptor(): ServerInterceptor {
            return MetadataServerInterceptor()
        }

        /**
         * 使用 builder 模式开启 gRPC 的限流能力
         *
         * @return [PolarisRateLimitInterceptorBuilder]
         */
        fun buildRateLimitInterceptor(): PolarisRateLimitInterceptorBuilder {
            return PolarisRateLimitInterceptorBuilder()
        }
    }
}
