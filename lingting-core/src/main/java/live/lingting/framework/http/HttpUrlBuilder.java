package live.lingting.framework.http;

import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.StringUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lingting 2024-01-29 16:13
 */
public class HttpUrlBuilder  {

	private final Map<String, List<String>> params = new HashMap<>();

	private String scheme = "https";

	private String host;

	private Integer port;

	private StringBuilder uri;

	public static HttpUrlBuilder builder() {
		return new HttpUrlBuilder();
	}

	public HttpUrlBuilder scheme(String scheme) {
		this.scheme = scheme;
		return this;
	}

	public HttpUrlBuilder http() {
		return scheme("http");
	}

	public HttpUrlBuilder https() {
		return scheme("https");
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
		return uri(new StringBuilder(uri));
	}

	public HttpUrlBuilder uri(StringBuilder uri) {
		this.uri = uri;
		return this;
	}

	public HttpUrlBuilder uriSegment(String... segments) {
		if (uri == null) {
			uri = new StringBuilder();
		}

		if (!uri.isEmpty() && !uri.substring(uri.length() - 1).equals("/")) {
			uri.append("/");
		}

		for (String segment : segments) {
			uri.append(segment).append("/");
		}

		return this;
	}

	public HttpUrlBuilder addParam(String name, Object value) {
		if (value == null) {
			return this;
		}
		if (value instanceof Map<?, ?> map) {
			map.forEach((k, v) -> addParam(k.toString(), v));
		}
		else if (CollectionUtils.isMulti(value)) {
			CollectionUtils.multiToList(value).forEach(o -> addParam(name, o));
		}
		else {
			List<String> list = params.computeIfAbsent(name, k -> new ArrayList<>());
			list.add(value.toString());
		}
		return this;
	}

	public HttpUrlBuilder addParams(Map<String, ?> params) {
		for (Map.Entry<String, ?> entry : params.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value == null) {
				continue;
			}
			addParam(key, value);
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

	public URI buildUri() {
		String string = build();
		return URI.create(string);
	}

	public HttpUrlBuilder copy() {
		return builder().scheme(scheme)
			.host(host)
			.port(port)
			.uri(uri == null ? null : uri.toString())
			.addParams(params);
	}

}
