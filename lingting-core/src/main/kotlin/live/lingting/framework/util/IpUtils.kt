package live.lingting.framework.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author psh 2022-04-21 16:55
 */
public final class IpUtils {

	public static final String LOCALHOST = "127.0.0.1";

	public static final String UNKNOWN = "UNKNOWN";

	public static final String MULTI_IP_SPLIT = ",";

	public static final String IPV4_SPLIT = ".";

	public static final String IPV6_SPLIT = ":";

	public static final Integer IPV4_LENGTH_MAX = 16;

	private static final List<String> HEADERS;
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(IpUtils.class);

	static {
		HEADERS = new ArrayList<>(16);
		HEADERS.add("X-Forwarded-For");
		HEADERS.add("Node-Forwarded-IP");
		HEADERS.add("X-Real-Ip");
		HEADERS.add("Proxy-Client-IP");
		HEADERS.add("WL-Proxy-Client-IP");
		HEADERS.add("HTTP_CLIENT_IP");
		HEADERS.add("HTTP_X_FORWARDED_FOR");
	}

	private IpUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static String getFirstIp(HttpServletRequest request) {
		String ip;
		for (String header : HEADERS) {
			// 处理IP
			ip = handlerIp(request.getHeader(header));
			if (ip != null) {
				return ip;
			}
		}

		return handlerIp(request.getRemoteAddr());
	}

	/**
	 * 处理IP, 可能是多个IP拼接, 处理成单个IP
	 */
	public static String handlerIp(String originIp) {
		if (isLocalhost(originIp)) {
			return LOCALHOST;
		}

		if (!StringUtils.hasText(originIp) || UNKNOWN.equalsIgnoreCase(originIp)) {
			return null;
		}

		if (originIp.contains(MULTI_IP_SPLIT)) {
			originIp = originIp.substring(0, originIp.indexOf(MULTI_IP_SPLIT));
		}

		if (originIp.length() >= IPV4_LENGTH_MAX) {
			originIp = originIp.substring(0, IPV4_LENGTH_MAX);
		}

		return originIp;
	}

	public static boolean isLocalhost(String ip) {
		if (!StringUtils.hasText(ip)) {
			return false;
		}
		return switch (ip) {
			case "[0:0:0:0:0:0:0:1]", "0:0:0:0:0:0:0:1", LOCALHOST, "localhost" -> true;
			default -> false;
		};
	}

	/**
	 * 是否为正确的IP地址
	 *
	 * @param raw       原始值
	 * @param predicate 附加判断,
	 * @return true 满足是IP地址和附加判断时返回true
	 */
	public static boolean isIp(String raw, Predicate<InetAddress> predicate) {
		if (!StringUtils.hasText(raw)) {
			return false;
		}
		try {
			String rawTrim = raw.trim();
			String rawNormalize = rawTrim.contains(IPV6_SPLIT) ? rawTrim.replaceAll("(^|:)0+(\\w+)", "$1$2")
				: rawTrim.replaceAll("(^|.)0+(\\w+)", "$1$2");

			InetAddress address = InetAddress.getByName(raw);
			String hostAddress = address.getHostAddress();
			// 解析出来的host地址与原始值一致则表示原始值为IP地址
			if (!Objects.equals(hostAddress, rawTrim) && !Objects.equals(hostAddress, rawNormalize)) {
				return false;
			}
			// 执行附加判断
			return predicate.test(address);
		}
		catch (Exception e) {
			return false;
		}
	}

	public static boolean isIp(String raw) {
		return isIp(raw, i -> true);
	}

	public static boolean isIpv4(String raw) {
		return isIp(raw, address -> address.getAddress().length == 4);
	}

	public static boolean isIpv6(String raw) {
		return isIp(raw, address -> address.getAddress().length == 16);
	}

	public static String resolve(String host) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(host);
		return address.getHostAddress();
	}

	public static List<String> list(HttpServletRequest request) {
		List<String> list = new ArrayList<>();

		for (String header : HEADERS) {
			String val = request.getHeader(header);
			if (StringUtils.hasText(val) && !UNKNOWN.equalsIgnoreCase(val)) {
				if (val.contains(MULTI_IP_SPLIT)) {
					list.addAll(Arrays.asList(val.split(MULTI_IP_SPLIT)));
				}
				else {
					list.add(val);
				}
			}
		}

		list.add(request.getRemoteAddr());
		return list;
	}

}
