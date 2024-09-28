package live.lingting.framework.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author lingting 2024-01-29 16:29
 */
@SuppressWarnings({"java:S5527", "java:S6548"})
public class HostnameAllVerifier implements HostnameVerifier {

	public static final HostnameAllVerifier INSTANCE = new HostnameAllVerifier();

	private HostnameAllVerifier() {
	}

	@Override
	public boolean verify(String hostname, SSLSession sslSession) {
		return true;
	}

}
