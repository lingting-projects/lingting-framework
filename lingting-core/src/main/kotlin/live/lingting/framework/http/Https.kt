package live.lingting.framework.http

import java.security.SecureRandom
import java.util.Objects
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author lingting 2024-09-28 11:32
 */
object Https {

    val SSL_DISABLED_TRUST_MANAGER: X509TrustManager = X509TrustAllManager


    val SSL_DISABLED_HOSTNAME_VERIFIER: HostnameVerifier = HostnameAllVerifier


    fun sslContext(tm: TrustManager, vararg tms: TrustManager): SSLContext {
        return sslContext("TLS", tm, tms)
    }

    fun sslContext(protocol: String, tm: TrustManager, tms: Array<out TrustManager>): SSLContext {
        val context = SSLContext.getInstance(protocol)
        val random = SecureRandom()
        val list: MutableList<TrustManager> = ArrayList()
        list.add(tm)
        tms.filter { Objects.nonNull(it) }.forEach { list.add(it) }
        context.init(null, list.toTypedArray<TrustManager>(), random)
        return context
    }

    fun sslDisabledContext(): SSLContext {
        return sslContext(SSL_DISABLED_TRUST_MANAGER)
    }

}
