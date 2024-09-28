package live.lingting.framework.http;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author lingting 2024-01-29 16:27
 */
@SuppressWarnings({"java:S4830", "java:S6548"})
public class X509TrustAllManager implements X509TrustManager {

	public static final X509TrustAllManager INSTANCE = new X509TrustAllManager();

	private X509TrustAllManager() {
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
		//
	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
		//
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

}
