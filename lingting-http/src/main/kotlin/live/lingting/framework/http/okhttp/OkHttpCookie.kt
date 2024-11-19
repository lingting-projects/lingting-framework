package live.lingting.framework.http.okhttp

import java.net.CookieStore
import java.net.HttpCookie
import java.util.function.Consumer
import okhttp3.Cookie
import okhttp3.Cookie.Builder
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * @author lingting 2024-05-08 13:59
 */
class OkHttpCookie(private val store: CookieStore) : CookieJar {
    fun of(cookie: HttpCookie): Cookie {
        val builder = Builder()
        builder.domain(cookie.domain)
        builder.expiresAt(cookie.maxAge * 1000 + System.currentTimeMillis())

        if (cookie.isHttpOnly) {
            builder.httpOnly()
        }

        builder.name(cookie.name)
        builder.path(cookie.path)

        if (cookie.secure) {
            builder.secure()
        }

        builder.value(cookie.value)
        return builder.build()
    }

    fun to(cookie: Cookie): HttpCookie {
        val hc = HttpCookie(cookie.name, cookie.value)
        hc.domain = cookie.domain
        hc.isHttpOnly = cookie.httpOnly
        hc.maxAge = (cookie.expiresAt - System.currentTimeMillis()) / 1000
        hc.path = cookie.path
        hc.secure = cookie.secure
        return hc
    }

    override fun loadForRequest(httpUrl: HttpUrl): List<Cookie> {
        val cookies = store[httpUrl.toUri()]
        return cookies.stream().map { cookie: HttpCookie -> this.of(cookie) }.toList()
    }

    override fun saveFromResponse(httpUrl: HttpUrl, list: List<Cookie>) {
        list.forEach(Consumer { cookie: Cookie -> store.add(httpUrl.toUri(), to(cookie)) })
    }
}
