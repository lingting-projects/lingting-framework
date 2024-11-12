package live.lingting.framework.ali.exception

/**
 * @author lingting 2024-09-12 21:40
 */
open class AliException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
