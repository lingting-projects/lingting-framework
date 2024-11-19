package live.lingting.framework.aws.exception

/**
 * @author lingting 2024-09-19 15:14
 */
open class AwsException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
