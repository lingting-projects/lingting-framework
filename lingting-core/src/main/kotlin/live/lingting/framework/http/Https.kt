package live.lingting.framework.http

import java.security.SecureRandom
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author lingting 2024-09-28 11:32
 */
class Https private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {

        val SSL_DISABLED_TRUST_MANAGER: X509TrustManager = X509TrustAllManager.INSTANCE


        val SSL_DISABLED_HOSTNAME_VERIFIER: HostnameVerifier = HostnameAllVerifier.INSTANCE


        fun sslContext(tm: TrustManager, vararg tms: TrustManager): SSLContext {
            val context = SSLContext.getInstance("TLS")
            val random = SecureRandom()
            val list: MutableList<TrustManager> = ArrayList()
            list.add(tm)
            Arrays.stream(tms).filter { obj: TrustManager? -> Objects.nonNull(obj) }.forEach { e: TrustManager -> list.add(e) }
            context.init(null, list.toTypedArray<TrustManager>(), random)
            return context
        }


        fun sslDisabledContext(): SSLContext {
            return sslContext(SSL_DISABLED_TRUST_MANAGER)
        }
    }
}
