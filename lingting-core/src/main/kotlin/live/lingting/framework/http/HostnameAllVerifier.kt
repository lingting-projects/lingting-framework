package live.lingting.framework.http

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * @author lingting 2024-01-29 16:29
 */
class HostnameAllVerifier private constructor() : HostnameVerifier {
    override fun verify(hostname: String, sslSession: SSLSession): Boolean {
        return true
    }

    companion object {
        val INSTANCE: HostnameAllVerifier = HostnameAllVerifier()
    }
}
