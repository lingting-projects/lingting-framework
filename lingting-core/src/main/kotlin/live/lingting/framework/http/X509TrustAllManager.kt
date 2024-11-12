package live.lingting.framework.http

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * @author lingting 2024-01-29 16:27
 */
class X509TrustAllManager private constructor() : X509TrustManager {

    override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, authType: String) {
        //
    }


    override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, authType: String) {
        //
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOfNulls(0)
    }

    companion object {
        val INSTANCE: X509TrustAllManager = X509TrustAllManager()
    }
}
