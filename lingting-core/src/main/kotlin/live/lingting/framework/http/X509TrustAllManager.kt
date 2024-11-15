package live.lingting.framework.http

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * @author lingting 2024-01-29 16:27
 */
@Suppress("kotlin:S5527", "kotlin:S6516")
object X509TrustAllManager : X509TrustManager {
    val ARRAY = arrayOf<X509Certificate>()

    override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, authType: String) {
        //
    }


    override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, authType: String) {
        //
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return ARRAY
    }

}
