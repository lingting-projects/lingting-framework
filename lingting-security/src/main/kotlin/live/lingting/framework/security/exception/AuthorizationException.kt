package live.lingting.framework.security.exception

/**
 * @author lingting 2023-03-29 21:41
 */
class AuthorizationException : SecurityException {
    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
