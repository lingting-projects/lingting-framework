package live.lingting.framework.exception

/**
 * @author lingting 2024-01-29 16:05
 */
class HttpException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)
}
