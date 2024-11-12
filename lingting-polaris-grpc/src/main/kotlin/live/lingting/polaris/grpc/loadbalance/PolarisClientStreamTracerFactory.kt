package live.lingting.polaris.grpc.loadbalance

import io.grpc.ClientStreamTracer
import io.grpc.ClientStreamTracer.StreamInfo
import io.grpc.Metadata
import live.lingting.polaris.grpc.util.ClientCallInfo

/**
 * Factory class for [ClientStreamTracer].
 *
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class PolarisClientStreamTracerFactory(private val callInfo: ClientCallInfo) : ClientStreamTracer.Factory() {
    override fun newClientStreamTracer(info: StreamInfo, headers: Metadata): ClientStreamTracer {
        return PolarisClientStreamTracer(callInfo)
    }
}
