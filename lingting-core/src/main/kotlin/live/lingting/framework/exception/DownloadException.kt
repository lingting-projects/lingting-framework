package live.lingting.framework.exception

/**
 * @author lingting 2024-01-16 19:41
 */
class DownloadException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
