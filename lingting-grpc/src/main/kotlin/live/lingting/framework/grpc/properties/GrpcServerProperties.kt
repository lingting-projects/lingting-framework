package live.lingting.framework.grpc.properties

import live.lingting.framework.util.MdcUtils
import java.time.Duration

/**
 * @author lingting 2023-04-06 15:18
 */
class GrpcServerProperties {
    var port: Int? = null

    var messageSize: Long = 524288

    var keepAliveTime: Duration? = Duration.ofMinutes(30)

    var keepAliveTimeout: Duration? = Duration.ofSeconds(2)

    var traceIdKey: String? = MdcUtils.TRACE_ID

    var traceOrder: Int = Int.MIN_VALUE + 100

    var exceptionHandlerOrder: Int = Int.MIN_VALUE + 200
}
