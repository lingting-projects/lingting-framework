package live.lingting.framework.okhttp;

import live.lingting.framework.util.CollectionUtils;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2022/11/17 16:42
 */
public class KeepCookieJar implements CookieJar {

	private final Map<String, Map<String, Cookie>> cache = new ConcurrentHashMap<>();

	protected Map<String, Cookie> cookieMap(String domain) {
		return cache.computeIfAbsent(domain, k -> new ConcurrentHashMap<>());
	}

	public Set<String> domains() {
		return cache.keySet();
	}

	public Collection<Cookie> cookies(String domain) {
		return cookieMap(domain).values();
	}

	public Cookie cookie(String domain, String name) {
		return cookieMap(domain).get(name);
	}

	@NotNull
	@Override
	public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
		String host = httpUrl.host();
		String privateDomain = httpUrl.topPrivateDomain();

		Collection<Cookie> hostCookies = cookies(host);
		Collection<Cookie> privateDomainCookies = cookies(privateDomain);

		List<Cookie> cookies = new ArrayList<>();
		if (!CollectionUtils.isEmpty(hostCookies)) {
			cookies.addAll(hostCookies);
		}
		if (!CollectionUtils.isEmpty(privateDomainCookies)) {
			cookies.addAll(privateDomainCookies);
		}
		return cookies;
	}

	@Override
	public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
		putAll(list);
	}

	public void put(Cookie cookie) {
		putAll(Collections.singleton(cookie));
	}

	public void putAll(Collection<Cookie> cookies) {
		for (Cookie cookie : cookies) {
			cookieMap(cookie.domain()).put(cookie.name(), cookie);
		}
	}

	public void clean(String domain) {
		cache.remove(domain);
	}

	public void cleanAll() {
		cache.clear();
	}

}
