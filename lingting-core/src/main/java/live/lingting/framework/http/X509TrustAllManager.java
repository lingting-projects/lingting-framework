package live.lingting.framework.http;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author lingting 2024-01-29 16:27
 */
@SuppressWarnings("java:S4830")
public class X509TrustAllManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		//
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
		//
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

}
