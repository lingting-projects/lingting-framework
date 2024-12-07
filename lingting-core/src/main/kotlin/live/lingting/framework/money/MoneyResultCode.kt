package live.lingting.framework.money

import live.lingting.framework.api.ResultCode

/**
 * 202201
 * @author lingting 2023-05-07 18:02
 */
enum class MoneyResultCode(override val code: Int, override val message: String) : ResultCode {
    /**
     * 金额值异常!
     */
    VALUE_ERROR(2022010000, "Money value error!"),

    /**
     * 金额配置异常!
     */
    CONFIG_ERROR(2022010001, "Money config error!"),

    ;
}
