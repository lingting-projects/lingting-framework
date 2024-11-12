
package live.lingting.polaris.grpc.util;

import com.tencent.polaris.api.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lixiaoshuang
 */
@SuppressWarnings("java:S1181")
public final class NetworkHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkHelper.class);

	private static final String LOCALHOST_VALUE = "127.0.0.1";

	private NetworkHelper() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	/**
	 * Gets the local address to which the socket is bound.
	 *
	 * @param host polaris server host
	 * @param port polaris server port
	 * @return local ip
	 */
	public static String getLocalHost(String host, int port) {
		try (Socket socket = new Socket(host, port)) {
			InetAddress address = socket.getLocalAddress();
			return address.getHostAddress();
		}
		catch (IOException ex) {
			LOGGER.error("getLocalHost through socket fail : {}", ex.getMessage());
			return getLocalHostExactAddress();
		}
	}

	/**
	 * Get real local ip.
	 * <p>
	 * You can use getNetworkInterfaces()+getInetAddresses() to get all the IP addresses
	 * of the node, and then judge to find out the site-local address, this is a
	 * recommended solution.
	 *
	 * @return real ip
	 */
	public static String getLocalHostExactAddress() {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface iface = networkInterfaces.nextElement();
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
					InetAddress inetAddr = inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress() && inetAddr.isSiteLocalAddress()) {
						return inetAddr.getHostAddress();
					}
				}
			}
			return getLocalHost();
		}
		catch (Exception e) {
			LOGGER.error("getLocalHostExactAddress error", e);
		}
		return null;
	}

	/**
	 * Get local ip.
	 * <p>
	 * There are environmental restrictions. Different environments may get different ips.
	 */
	public static String getLocalHost() {
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
		}
		catch (Throwable e) {
			LOGGER.error("get local host", e);
		}
		if (inetAddress == null) {
			return LOCALHOST_VALUE;
		}
		return inetAddress.getHostAddress();
	}

	public static Map<String, String> getUrlParams(String param) {
		Map<String, String> map = new HashMap<>();
		if (StringUtils.isBlank(param)) {
			return map;
		}
		String[] params = param.split("&");
		for (String s : params) {
			String[] p = s.split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}
		return map;
	}

}
