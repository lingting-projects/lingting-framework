package live.lingting.framework.grpc.properties

import java.time.Duration
import live.lingting.framework.util.MdcUtils

/**
 * @author lingting 2023-04-06 15:18
 */
class GrpcClientProperties {
    var host: String? = null

    var port: Int = 80

    var traceIdKey: String = MdcUtils.TRACE_ID

    var traceOrder: Int = Int.MIN_VALUE + 100

    var isUsePlaintext: Boolean = false

    /**
     * 是否关闭ssl校验,仅在不使用明文时生效
     */
    var isDisableSsl: Boolean = false

    var isEnableRetry: Boolean = true

    var isEnableKeepAlive: Boolean = true

    var keepAliveTime: Duration = Duration.ofMinutes(30)

    var keepAliveTimeout: Duration = Duration.ofSeconds(2)
}
