package live.lingting.framework.http.exception

import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpResponse

/**
 * @author lingting 2024-01-29 16:05
 */
class HttpException : RuntimeException {

    @JvmOverloads
    constructor(message: String?, cause: Throwable? = null) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    @JvmOverloads
    constructor(request: HttpRequest, message: String? = null, cause: Throwable? = null) : this("${request.method()} ${request.uri()} $message", cause)

    constructor(request: HttpRequest, cause: Throwable?) : this(request, null, cause)

    @JvmOverloads
    constructor(response: HttpResponse, message: String? = null, cause: Throwable? = null) : this("${response.code()} ${response.request().method()} ${response.uri()} $message", cause)

    constructor(response: HttpResponse, cause: Throwable?) : this(response, null, cause)

}
