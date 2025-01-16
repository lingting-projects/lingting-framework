package live.lingting.framework.exception

import live.lingting.framework.api.ResultCode

/**
 * @author lingting 2022/9/22 12:11
 */
class BizException : RuntimeException {

    companion object {

        @JvmStatic
        @JvmOverloads
        fun format(result: ResultCode, message: String? = null): String {
            return "[${result.code}] ${message ?: result.i18nMessage()}"
        }

    }

    val result: ResultCode

    val code: Int

    constructor(result: ResultCode) : this(result, null as String?)

    constructor(result: ResultCode, e: Exception? = null) : this(result, null, e)

    constructor(result: ResultCode, message: String?, e: Exception? = null) : super(format(result, message), e) {
        this.result = result
        this.code = result.code
    }

    override val message: String
        get() = super.message ?: format(result)
}
