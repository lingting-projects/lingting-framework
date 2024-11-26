package live.lingting.framework.grpc.properties

import java.time.Duration
import live.lingting.framework.data.DataSize
import live.lingting.framework.util.MdcUtils

/**
 * @author lingting 2023-04-06 15:18
 */
class GrpcServerProperties {
    var port: Int? = null

    var messageSize: Long = DataSize.ofMb(1).bytes

    var keepAliveTime: Duration = Duration.ofMinutes(30)

    var keepAliveTimeout: Duration = Duration.ofSeconds(2)

    var traceIdKey: String = MdcUtils.TRACE_ID

    var traceOrder: Int = Int.MIN_VALUE + 100

    var exceptionHandlerOrder: Int = Int.MIN_VALUE + 200
}
