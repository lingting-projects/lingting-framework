package live.lingting.framework.util

import live.lingting.framework.util.Slf4jUtils.logger
import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI

/**
 * @author lingting 2025/8/2 15:31
 */
object ProxySelectorUtils {
    private val log = logger()

    @JvmStatic
    fun create(sa: SocketAddress): ProxySelector {
        val proxy = Proxy(Proxy.Type.HTTP, sa)
        return create(proxy)
    }

    @JvmStatic
    fun create(p: Proxy): ProxySelector {
        val proxies = listOf(p)
        return object : ProxySelector() {
            override fun select(uri: URI?): List<Proxy?>? {
                return proxies
            }

            override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
                log.warn("proxy connect failed! uri: {}; address: {};", uri, sa, ioe)
            }

        }
    }
}
