package live.lingting.framework.datascope.exception

/**
 * @author lingting 2024-01-19 15:54
 */
class DataScopeException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
