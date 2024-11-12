package live.lingting.framework.ntp

/**
 * @author lingting 2022/11/24 10:13
 */
class NtpException : RuntimeException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
