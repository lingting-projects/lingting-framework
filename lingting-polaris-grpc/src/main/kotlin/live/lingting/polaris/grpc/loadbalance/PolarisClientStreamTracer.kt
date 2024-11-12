package live.lingting.polaris.grpc.loadbalance

import com.tencent.polaris.api.exception.PolarisException
import com.tencent.polaris.api.pojo.RetStatus
import com.tencent.polaris.api.rpc.ServiceCallResult
import io.grpc.ClientStreamTracer
import io.grpc.Status
import live.lingting.polaris.grpc.util.ClientCallInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * grpc 调用的 tracer 信息，记录每次 grpc 调用的情况 1. 每次请求的相应时间 2. 每次请求的结果，记录成功或者失败
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisClientStreamTracer(private val info: ClientCallInfo) : ClientStreamTracer() {
    private val perStreamRequestStartTimes: MutableMap<Int, Long> = ConcurrentHashMap()

    private val result = ServiceCallResult()

    init {
        result.host = info.instance.host
        result.port = info.instance.port
        result.method = info.method
        result.namespace = info.targetNamespace
        result.service = info.targetService
    }

    override fun outboundMessage(seqNo: Int) {
        perStreamRequestStartTimes[seqNo] = System.currentTimeMillis()
    }

    override fun inboundMessage(seqNo: Int) {
        val startTime = perStreamRequestStartTimes[seqNo]
        if (Objects.isNull(startTime)) {
            return
        }
        result.retStatus = RetStatus.RetSuccess
        result.retCode = Status.OK.code.value()
        result.delay = System.currentTimeMillis() - startTime!!

        try {
            info.consumerAPI.updateServiceCallResult(result)
        } catch (e: PolarisException) {
            LOG.error("[grpc-polaris] do report invoke call ret fail in inboundMessageRead", e)
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(PolarisClientStreamTracer::class.java)
    }
}
