package live.lingting.framework.datascope.exception

/**
 * @author lingting 2024-01-19 15:54
 */
class DataScopeException : RuntimeException {

    @JvmOverloads
    constructor(message: String?, cause: Throwable? = null) : super(message, cause)

}
