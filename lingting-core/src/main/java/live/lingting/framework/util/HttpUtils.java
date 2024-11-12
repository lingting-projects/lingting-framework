package live.lingting.framework.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author lingting 2022/10/28 17:54
 */
public final class HttpUtils {

	public static final String HEADER_HOST = "Host";

	public static final String HEADER_ORIGIN = "Origin";

	public static final String HEADER_USER_AGENT = "User-Agent";

	public static final String HEADER_AUTHORIZATION = "Authorization";

	public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

	private HttpUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static String host(HttpServletRequest request) {
		return request.getHeader(HEADER_HOST);
	}

	public static String origin(HttpServletRequest request) {
		return request.getHeader(HEADER_ORIGIN);
	}

	public static String language(HttpServletRequest request) {
		return request.getHeader(HEADER_ACCEPT_LANGUAGE);
	}

	public static String authorization(HttpServletRequest request) {
		return request.getHeader(HEADER_AUTHORIZATION);
	}

	public static String userAgent(HttpServletRequest request) {
		return request.getHeader(HEADER_USER_AGENT);
	}

}
