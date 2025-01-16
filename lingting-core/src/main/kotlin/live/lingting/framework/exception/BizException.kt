package live.lingting.framework.exception

import live.lingting.framework.api.ResultCode

/**
 * @author lingting 2022/9/22 12:11
 */
class BizException : RuntimeException {

    val result: ResultCode

    val code: Int

    constructor(result: ResultCode) : this(result, null as String?)

    constructor(result: ResultCode, e: Exception? = null) : this(result, null, e)

    constructor(result: ResultCode, message: String?, e: Exception? = null) : super("[${result.code}] ${message ?: result.i18nMessage()}", e) {
        this.result = result
        this.code = result.code
    }

}
