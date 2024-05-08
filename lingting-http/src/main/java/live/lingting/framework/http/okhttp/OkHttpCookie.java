package live.lingting.framework.http.okhttp;

import lombok.RequiredArgsConstructor;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;

/**
 * @author lingting 2024-05-08 13:59
 */
@RequiredArgsConstructor
public class OkHttpCookie implements CookieJar {

	private final CookieStore store;

	Cookie of(HttpCookie cookie) {
		Cookie.Builder builder = new Cookie.Builder();
		builder.domain(cookie.getDomain());
		builder.expiresAt(cookie.getMaxAge() * 1000 + System.currentTimeMillis());

		if (cookie.isHttpOnly()) {
			builder.httpOnly();
		}

		builder.name(cookie.getName());
		builder.path(cookie.getPath());

		if (cookie.getSecure()) {
			builder.secure();
		}

		builder.value(cookie.getValue());
		return builder.build();
	}

	HttpCookie to(Cookie cookie) {
		HttpCookie hc = new HttpCookie(cookie.name(), cookie.value());
		hc.setDomain(cookie.domain());
		hc.setHttpOnly(cookie.httpOnly());
		hc.setMaxAge((cookie.expiresAt() - System.currentTimeMillis()) / 1000);
		hc.setPath(cookie.path());
		hc.setSecure(cookie.secure());
		return hc;
	}

	@NotNull
	@Override
	public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
		List<HttpCookie> cookies = store.get(httpUrl.uri());
		return cookies.stream().map(this::of).toList();
	}

	@Override
	public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
		list.forEach(cookie -> store.add(httpUrl.uri(), to(cookie)));
	}

}
