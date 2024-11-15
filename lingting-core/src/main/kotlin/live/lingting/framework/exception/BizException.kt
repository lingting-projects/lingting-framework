package live.lingting.framework.exception

import live.lingting.framework.api.ResultCode

/**
 * @author lingting 2022/9/22 12:11
 */
class BizException constructor(val code: Int?, override val message: String?, e: Exception? = null) : RuntimeException(message, e) {
    constructor(resultCode: ResultCode) : this(resultCode.code, resultCode.message, null)

    constructor(resultCode: ResultCode, e: Exception?) : this(resultCode, resultCode.message, e)

    constructor(resultCode: ResultCode, message: String?, e: Exception?) : this(resultCode.code, message, e)
}
