package live.lingting.framework.money

import live.lingting.framework.exception.BizException
import java.math.RoundingMode

/**
 * @author lingting 2023-05-07 17:55
 */
class MoneyConfig(
    /**
     * 小数位数量, 如果值非大于0则舍弃小数
     */
    val decimalLimit: Int,
    /**
     * 小数位处理方案, 当小数位数量值有效时使用, 如果值为空则使用 [Money.DEFAULT_DECIMAL_TYPE]
     */
    val decimalType: RoundingMode,
    /**
     * 分位间隔数量, 如果值非大于0则不进行分位处理
     *
     *
     * 2: 百分位
     *
     *
     *
     * 3: 千分位
     *
     *
     *
     * 4: 万分位
     *
     */
    val quantileLimit: Int?,
    /**
     * 分位符号
     */
    val quantileSymbol: String
) {
    companion object {
        /**
         * 默认金额配置
         */
        val DEFAULT: MoneyConfig = MoneyConfig(2, RoundingMode.HALF_UP, null, "")

        /**
         * 小数位数量是否有效
         *
         * @return true 有效, 需要进行小数位处理
         */
        fun validDecimal(decimalLimit: Int?): Boolean {
            return decimalLimit != null && decimalLimit > 0
        }

        /**
         * 分位配置是否有效
         *
         * @return true 表示有效, 需要进行分位控制
         */
        fun validQuantile(quantileLimit: Int?): Boolean {
            return quantileLimit != null && quantileLimit > 0
        }

        /**
         * 校验分位配置
         */
        fun validQuantile(quantileLimit: Int?, quantileSymbol: String) {
            // 无效分位配置不管
            if (!validQuantile(quantileLimit)) {
                return
            }
            // 有效的分位配置 , 分位符号必须正常
            if (quantileSymbol == null || quantileSymbol.isEmpty()) {
                throw BizException(MoneyResultCode.CONFIG_ERROR)
            }
        }
    }
}
