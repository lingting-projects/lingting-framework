package live.lingting.framework.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author lingting 2024-01-29 16:29
 */
@SuppressWarnings("java:S5527")
public class HostnameAllVerifier implements HostnameVerifier {

	@Override
	public boolean verify(String hostname, SSLSession sslSession) {
		return true;
	}

}
