package live.lingting.framework.http

/**
 * @author lingting 2024-05-07 17:20
 */
interface ResponseCallback {
    fun onError(request: HttpRequest?, e: Throwable?)


    fun onResponse(response: HttpResponse?)
}
