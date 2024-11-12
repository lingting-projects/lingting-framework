package live.lingting.framework.http;

import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.StringUtils;
import live.lingting.framework.value.MultiValue;
import live.lingting.framework.value.multi.StringMultiValue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static live.lingting.framework.util.StringUtils.deleteLast;

/**
 * @author lingting 2024-01-29 16:13
 */
@SuppressWarnings("unchecked")
public class HttpUrlBuilder {

	protected final StringMultiValue params = new StringMultiValue();

	protected String scheme = "https";

	protected String host;

	protected Integer port;

	protected StringBuilder uri = new StringBuilder("/");

	public static HttpUrlBuilder builder() {
		return new HttpUrlBuilder();
	}

	public static HttpUrlBuilder from(String url) {
		URI u = URI.create(url);
		return from(u);
	}

	public static HttpUrlBuilder from(URI u) {
		HttpUrlBuilder builder = builder().scheme(u.getScheme()).host(u.getHost()).port(u.getPort()).uri(u.getPath());
		String query = u.getQuery();
		if (StringUtils.hasText(query)) {
			Arrays.stream(query.split("&")).forEach(s -> {
				String[] split = s.split("=", 2);
				String name = split[0];
				builder.addParam(name, split.length == 1 ? null : split[1]);
			});
		}
		return builder;
	}

	public static String buildQuery(MultiValue<String, String, ?> value) {
		return buildQuery((Map<String, Collection<String>>) value.map());
	}

	public static String buildQuery(Map<String, Collection<String>> map) {
		if (CollectionUtils.isEmpty(map)) {
			return "";
		}
		List<String> keys = map.keySet().stream().sorted().toList();

		StringBuilder builder = new StringBuilder();
		for (String key : keys) {
			Collection<String> list = map.get(key);
			if (CollectionUtils.isEmpty(list)) {
				builder.append(key).append("&");
			}
			else {
				for (String v : list) {
					builder.append(key).append("=").append(v).append("&");
				}
			}
		}

		return deleteLast(builder).toString();
	}

	public StringMultiValue params() {
		return params.unmodifiable();
	}

	public String scheme() {
		return scheme;
	}

	public String host() {
		return host;
	}

	public Integer port() {
		return port;
	}

	public String uri() {
		return uri.toString();
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
		if (host.contains("://")) {
			String[] split = host.split("://");
			scheme(split[0]);
			this.host = split[1];
		}
		else {
			this.host = host;
		}
		return this;
	}

	public HttpUrlBuilder port(Integer port) {
		this.port = port;
		return this;
	}

	public HttpUrlBuilder uri(String string) {
		if (!StringUtils.hasText(string)) {
			this.uri = new StringBuilder("/");
			return this;
		}
		String newUri;
		String query;
		if (string.contains("?")) {
			String[] split = string.split("\\?", 2);
			newUri = split[0];
			query = split[1];
		}
		else {
			newUri = string;
			query = "";
		}
		this.uri = new StringBuilder(newUri);
		if (StringUtils.hasText(query)) {
			String[] split = query.split("&");
			for (String kv : split) {
				String[] array = kv.split("=", 2);
				String name = array[0];
				String value = array.length > 1 ? array[1] : null;
				addParam(name, value);
			}
		}
		return this;
	}

	public HttpUrlBuilder uri(StringBuilder uri) {
		return uri(uri.toString());
	}

	public HttpUrlBuilder uriSegment(String... segments) {
		if (!uri.isEmpty() && !uri.substring(uri.length() - 1).equals("/")) {
			uri.append("/");
		}

		for (String segment : segments) {
			uri.append(segment).append("/");
		}

		return this;
	}

	public HttpUrlBuilder addParam(String name, Object value) {
		params.ifAbsent(name);
		if (value instanceof Map<?, ?> map) {
			map.forEach((k, v) -> addParam(k.toString(), v));
		}
		else if (CollectionUtils.isMulti(value)) {
			List<Object> list = CollectionUtils.multiToList(value);
			list.forEach(o -> addParam(name, o));
		}
		else if (value != null) {
			params.add(name, value.toString());
		}
		return this;
	}

	public HttpUrlBuilder addParams(Map<String, ?> params) {
		params.forEach(this::addParam);
		return this;
	}

	public HttpUrlBuilder addParams(MultiValue<String, ?, ?> params) {
		params.forEach(this::addParam);
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
		builder.append(scheme).append("://");
		builder.append(host);
		if (host.endsWith("/")) {
			builder.deleteCharAt(builder.length() - 1);
		}
		if (port != null) {
			builder.append(":").append(port);
		}
		builder.append(buildPath());
		String query = buildQuery();
		if (StringUtils.hasText(query)) {
			if (builder.charAt(builder.length() - 1) != '?') {
				builder.append("?");
			}
			builder.append(query);
		}
		return builder.toString();
	}

	public String buildPath() {
		if (!StringUtils.hasText(uri)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		String string = uri.toString();
		if (!string.startsWith("/")) {
			builder.append("/");
		}
		builder.append(string);
		if (string.endsWith("/")) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public String buildQuery() {
		return buildQuery(params);
	}

	public URI buildUri() {
		try {
			String path = buildPath();
			String query = buildQuery();
			int p = port == null ? -1 : port;
			String q = StringUtils.hasText(query) ? query : null;
			return new URI(scheme, null, host, p, path, q, null);
		}
		catch (URISyntaxException e) {
			throw new IllegalStateException("Could not create URI object: " + e.getMessage(), e);
		}
	}

	public URL buildUrl() throws MalformedURLException {
		return buildUri().toURL();
	}

	public HttpUrlBuilder copy() {
		return builder().scheme(scheme).host(host).port(port).uri(uri).addParams(params);
	}

}
