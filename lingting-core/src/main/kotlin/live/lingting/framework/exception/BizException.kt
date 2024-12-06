package live.lingting.framework.exception

import live.lingting.framework.api.ResultCode

/**
 * @author lingting 2022/9/22 12:11
 */
class BizException @JvmOverloads constructor(
    val code: Int,
    override val message: String,
    e: Exception? = null
) : RuntimeException(message, e) {

    constructor(resultCode: ResultCode) : this(resultCode, null as String?)

    constructor(resultCode: ResultCode, e: Exception? = null) : this(resultCode, null, e)

    constructor(resultCode: ResultCode, message: String?, e: Exception? = null) : this(resultCode.code, message ?: resultCode.i18nMessage(), e)

}
