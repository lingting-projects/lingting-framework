package live.lingting.framework.http;

import lombok.experimental.UtilityClass;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author lingting 2024-09-28 11:32
 */
@UtilityClass
public class Https {

	public static final X509TrustManager SSL_DISABLED_TRUST_MANAGER = X509TrustAllManager.INSTANCE;

	public static final HostnameVerifier SSL_DISABLED_HOSTNAME_VERIFIER = HostnameAllVerifier.INSTANCE;

	public static SSLContext sslContext(TrustManager tm, TrustManager... tms)
			throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext context = SSLContext.getInstance("TLS");
		SecureRandom random = new SecureRandom();
		List<TrustManager> list = new ArrayList<>();
		list.add(tm);
		Arrays.stream(tms).filter(Objects::nonNull).forEach(list::add);
		context.init(null, list.toArray(new TrustManager[0]), random);
		return context;
	}

	public static SSLContext sslDisabledContext() throws NoSuchAlgorithmException, KeyManagementException {
		return sslContext(SSL_DISABLED_TRUST_MANAGER);
	}

}
