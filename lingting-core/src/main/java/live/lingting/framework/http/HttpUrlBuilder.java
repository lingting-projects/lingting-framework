package live.lingting.framework.http;

import live.lingting.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingting 2024-01-29 16:13
 */
public class HttpUrlBuilder {

	private String scheme = "https";

	private String host;

	private Integer port;

	private String uri;

	private final Map<String, List<String>> params = new HashMap<>();

	public static HttpUrlBuilder builder() {
		return new HttpUrlBuilder();
	}

	public HttpUrlBuilder http() {
		this.scheme = "http";
		return this;
	}

	public HttpUrlBuilder https() {
		this.scheme = "https";
		return this;
	}

	public HttpUrlBuilder host(String host) {
		this.host = host;
		return this;
	}

	public HttpUrlBuilder port(Integer port) {
		this.port = port;
		return this;
	}

	public HttpUrlBuilder uri(String uri) {
		this.uri = uri;
		return this;
	}

	public HttpUrlBuilder addParam(String name, String value) {
		params.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
		return this;
	}

	public HttpUrlBuilder addParams(Map<String, ?> params) {
		for (Map.Entry<String, ?> entry : params.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value == null) {
				continue;
			}
			addParam(key, value.toString());
		}
		return this;
	}

	public String build() {
		if (!StringUtils.hasText(host)) {
			throw new IllegalArgumentException("Host [%s] is invalid!".formatted(host));
		}
		if (port != null && (port < 0 || port > 65535)) {
			throw new IllegalArgumentException("Port [%d] is invalid!".formatted(port));
		}

		StringBuilder builder = new StringBuilder();

		if (!host.startsWith("http")) {
			builder.append(scheme).append("://");
		}
		builder.append(host);
		if (port != null) {
			builder.append(":").append(port);
		}
		if (!host.endsWith("/")) {
			builder.append("/");
		}
		if (StringUtils.hasText(uri)) {
			builder.append(uri);
		}
		if (builder.charAt(builder.length() - 1) != '?') {
			builder.append("?");
		}

		for (Map.Entry<String, List<String>> entry : params.entrySet()) {
			String field = entry.getKey();

			for (String v : entry.getValue()) {
				builder.append("&").append(field).append("=").append(v);
			}
		}

		return builder.toString();
	}

}
