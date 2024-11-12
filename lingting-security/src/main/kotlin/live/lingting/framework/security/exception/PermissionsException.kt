package live.lingting.framework.security.exception

/**
 * 权限异常, 不满足指定访问权限
 *
 * @author lingting 2023-03-29 20:57
 */
class PermissionsException : SecurityException {
    constructor() : super()

    constructor(s: String?) : super(s)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)
}
