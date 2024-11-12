package live.lingting.polaris.grpc.server

import com.tencent.polaris.client.api.SDKContext
import io.grpc.Server
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class GraceOffline(private val grpcServer: Server, private val maxWaitDuration: Duration?, private val context: SDKContext?) {
    private val executed = AtomicBoolean(false)

    fun shutdown(): Server {
        if (!executed.compareAndSet(false, true)) {
            return grpcServer
        }
        LOGGER.info("[grpc-polaris] begin grace shutdown")
        grpcServer.shutdown()

        try {
            // 等待 4 个 pull 时间间隔
            TimeUnit.SECONDS.sleep(8)
        } catch (ignore: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        try {
            grpcServer.awaitTermination(maxWaitDuration!!.toMillis(), TimeUnit.MILLISECONDS)
        } catch (ignore: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        context!!.close()
        return grpcServer
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(GraceOffline::class.java)
    }
}
