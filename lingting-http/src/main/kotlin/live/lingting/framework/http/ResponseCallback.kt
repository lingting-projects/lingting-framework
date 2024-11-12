package live.lingting.framework.http

import okhttp3.Cookie.Builder.value

/**
 * @author lingting 2024-05-07 17:20
 */
interface ResponseCallback {
    fun onError(request: HttpRequest?, e: Throwable?)

    @Throws(Throwable::class)
    fun onResponse(response: HttpResponse?)
}
