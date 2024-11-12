package live.lingting.framework.security.exception

/**
 * @author lingting 2023-03-29 20:57
 */
abstract class SecurityException : RuntimeException {
    protected constructor() : super("")

    protected constructor(message: String?) : super(message)

    protected constructor(message: String?, cause: Throwable?) : super(message, cause)

    protected constructor(cause: Throwable?) : super("", cause)

    protected constructor(
        message: String?, cause: Throwable?, enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, cause, enableSuppression, writableStackTrace)
}
