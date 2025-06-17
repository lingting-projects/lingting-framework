package live.lingting.framework.grpc.properties

import java.time.Duration
import live.lingting.framework.data.DataSize
import live.lingting.framework.util.MdcUtils

/**
 * @author lingting 2023-04-06 15:18
 */
class GrpcServerProperties {
    var port: Int? = null

    /**
     * 是否使用gzip压缩/解压
     */
    var useGzip: Boolean = false

    var messageSize: Long = DataSize.ofMb(1).bytes

    var keepAliveTime: Duration = Duration.ofMinutes(30)

    var keepAliveTimeout: Duration = Duration.ofSeconds(2)

    var traceIdKey: String = MdcUtils.traceIdKey

    var traceOrder: Int = Int.MIN_VALUE

    var exceptionHandlerOrder: Int = Int.MIN_VALUE + 2000

    /**
     * 当上下文在关闭阶段时, 是否拒绝请求
     * @see live.lingting.framework.application.ApplicationHolder.isStop
     */
    var rejectRequestOnStop = true

}
