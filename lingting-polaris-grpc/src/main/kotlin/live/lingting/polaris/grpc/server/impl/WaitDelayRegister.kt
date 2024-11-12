package live.lingting.polaris.grpc.server.impl

import live.lingting.polaris.grpc.server.DelayRegister
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * @author [liaochuntao](mailto:liaochuntao@live.com)
 */
class WaitDelayRegister(private val waitTime: Duration) : DelayRegister {
    override fun allowRegis(): Boolean {
        try {
            TimeUnit.SECONDS.sleep(waitTime.seconds)
        } catch (ignore: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        return true
    }
}
