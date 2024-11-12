package live.lingting.framework.huawei.exception

/**
 * @author lingting 2024-09-12 21:41
 */
class HuaweiObsException : HuaweiException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
