package live.lingting.framework.money

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Objects

/**
 * @author lingting 2023-05-07 17:44
 */
class Money private constructor(
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
     * 2: 百分位
     * 3: 千分位
     * 4: 万分位
     */
    val quantileLimit: Int?,
    /**
     * 分位符号
     */
    val quantileSymbol: String?,
    /**
     * 值
     */
    val value: BigDecimal
) {
    // region 金额变动操作
    /**
     * 切换金额
     * @param value 值
     * @return this
     */
    fun use(value: BigDecimal): Money {
        return ofPrivate(value, decimalLimit, decimalType, quantileLimit, quantileSymbol)
    }

    /**
     * 增加
     * @param money 金额
     * @return 增加指定金额后的新金额
     */
    fun add(money: Money): Money {
        return add(money.value)
    }

    /**
     * 增加
     * @param money 金额
     * @return 增加指定金额后的新金额
     */
    fun add(money: Long): Money {
        return add(BigDecimal.valueOf(money))
    }

    /**
     * 增加
     * @param money 金额
     * @return 增加指定金额后的新金额
     */
    fun add(money: Double): Money {
        return add(BigDecimal.valueOf(money))
    }

    /**
     * 增加
     * @param money 金额
     * @return 增加指定金额后的新金额
     */
    fun add(money: BigDecimal): Money {
        return use(value.add(money))
    }

    /**
     * 减少
     * @param money 金额
     * @return 减少指定金额后的新金额
     */
    fun subtract(money: Money): Money {
        return subtract(money.value)
    }

    /**
     * 减少
     * @param money 金额
     * @return 减少指定金额后的新金额
     */
    fun subtract(money: Long): Money {
        return subtract(BigDecimal.valueOf(money))
    }

    /**
     * 减少
     * @param money 金额
     * @return 减少指定金额后的新金额
     */
    fun subtract(money: Double): Money {
        return subtract(BigDecimal.valueOf(money))
    }

    /**
     * 减少
     * @param money 金额
     * @return 减少指定金额后的新金额
     */
    fun subtract(money: BigDecimal): Money {
        return use(value.subtract(money))
    }

    /**
     * 乘以
     * @param money 金额
     * @return 乘以指定金额后的新金额
     */
    fun multiply(money: Money): Money {
        return multiply(money.value)
    }

    /**
     * 乘以
     * @param money 金额
     * @return 乘以指定金额后的新金额
     */
    fun multiply(money: Long): Money {
        return multiply(BigDecimal.valueOf(money))
    }

    /**
     * 乘以
     * @param money 金额
     * @return 乘以指定金额后的新金额
     */
    fun multiply(money: Double): Money {
        return multiply(BigDecimal.valueOf(money))
    }

    /**
     * 乘以
     * @param money 金额
     * @return 乘以指定金额后的新金额
     */
    fun multiply(money: BigDecimal): Money {
        return use(value.multiply(money))
    }

    /**
     * 除以
     * @param money 金额
     * @return 除以指定金额后的新金额
     */
    fun divide(money: Money): Money {
        return divide(money.value)
    }

    /**
     * 除以
     * @param money 金额
     * @return 除以指定金额后的新金额
     */
    fun divide(money: Long): Money {
        return divide(BigDecimal.valueOf(money))
    }

    /**
     * 除以
     * @param money 金额
     * @return 除以指定金额后的新金额
     */
    fun divide(money: Double): Money {
        return divide(BigDecimal.valueOf(money))
    }

    /**
     * 除以
     * @param money 金额
     * @return 除以指定金额后的新金额
     */
    fun divide(money: BigDecimal): Money {
        return use(value.divide(money, decimalType))
    }

    /**
     * 取反
     */
    fun negate(): Money {
        return use(value.negate())
    }

    // endregion

    // region 金额比对操作
    val isZero: Boolean
        /**
         * 是否为0
         * @return boolean
         */
        get() = value.compareTo(BigDecimal.ZERO) == 0

    val isNegative: Boolean
        /**
         * 是否为负数
         * @return boolean
         */
        get() = value.compareTo(BigDecimal.ZERO) < 0

    /**
     * 大于
     * @param money 金额
     * @return boolean true 表示大于目标金额
     */
    fun isGt(money: Money): Boolean {
        return isGt(money.value)
    }

    /**
     * 大于
     * @param money 金额
     * @return boolean true 表示大于目标金额
     */
    fun isGt(money: BigDecimal): Boolean {
        return value.compareTo(money) > 0
    }

    /**
     * 大于等于
     * @param money 金额
     * @return boolean true 表示大于等于目标金额
     */
    fun isGe(money: Money): Boolean {
        return isGe(money.value)
    }

    /**
     * 大于等于
     * @param money 金额
     * @return boolean true 表示大于等于目标金额
     */
    fun isGe(money: BigDecimal): Boolean {
        return value.compareTo(money) > -1
    }

    /**
     * 等于
     * @param money 金额
     * @return boolean true 表示等于目标金额
     */
    fun isEquals(money: Money): Boolean {
        return isEquals(money.value)
    }

    /**
     * 等于
     * @param money 金额
     * @return boolean true 表示等于目标金额
     */
    fun isEquals(money: BigDecimal): Boolean {
        return value.compareTo(money) == 0
    }

    /**
     * 小于
     * @param money 金额
     * @return boolean true 表示小于目标金额
     */
    fun isLt(money: Money): Boolean {
        return isLt(money.value)
    }

    /**
     * 小于
     * @param money 金额
     * @return boolean true 表示小于目标金额
     */
    fun isLt(money: BigDecimal): Boolean {
        return value.compareTo(money) < 0
    }

    /**
     * 小于等于
     * @param money 金额
     * @return boolean true 表示小于等于目标金额
     */
    fun isLe(money: Money): Boolean {
        return isLe(money.value)
    }

    /**
     * 小于等于
     * @param money 金额
     * @return boolean true 表示小于等于目标金额
     */
    fun isLe(money: BigDecimal): Boolean {
        return value.compareTo(money) < 1
    }

    // endregion

    // region 值处理
    /**
     * 返回原始值, 剔除无用的小数位
     */
    fun toRawString(): String {
        return value.stripTrailingZeros().toPlainString()
    }

    /**
     * 转化为指定配置对应的值.
     * 存在分位配置则加入嵌入分位
     * @return java.lang.String 字符串值
     */
    fun toPlainString(): String {
        // 不用处理小数位, 每次计算结果都会进行处理
        val plainString = toRawString()

        val limit = quantileLimit
        // 分位配置有效时解析
        if (MoneyConfig.validQuantile(limit)) {
            val split: Array<String> = plainString.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            // 整数位
            val integer = split[0]

            val builder = StringBuilder()
            var index = 1
            // 倒序处理整数位
            for (i in integer.length - 1 downTo 0) {
                // 放入到第一位
                builder.insert(0, integer[i])
                // 需要插入分位符号(必须不是最后一位)
                if (index % limit!! == 0 && i != 0) {
                    builder.insert(0, quantileSymbol)
                }
                index++
            }

            // 存在小数位则追加
            if (split.size > 1) {
                builder.append(".").append(split[1])
            }
            return builder.toString()
        }

        return plainString
    }

    /**
     * 返回数据的值
     * @return java.lang.String
     */
    override fun toString(): String {
        return toPlainString()
    }

    // endregion

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val money = o as Money
        return Objects.equals(value, money.value)
    }

    override fun hashCode(): Int {
        return Objects.hash(value)
    }

    companion object {
        @JvmField
        val DEFAULT_DECIMAL_TYPE: RoundingMode = RoundingMode.HALF_UP

        @JvmField
        val ZERO: Money = of(0)

        @JvmField
        val TEN: Money = of(10)

        @JvmField
        val HUNDRED: Money = of(100)

        /**
         * 通过指定金额值和上下文的配置进行构建
         * @param value 金额值
         * @return 金额实例
         */
        @JvmStatic
        fun of(value: String): Money {
            return of(BigDecimal(value))
        }

        /**
         * 通过指定金额值和上下文的配置进行构建
         * @param value 金额值
         * @return 金额实例
         */
        @JvmStatic
        fun of(value: Long): Money {
            return of(BigDecimal.valueOf(value))
        }

        /**
         * 通过指定金额值和上下文的配置进行构建
         * @param value 金额值
         * @return 金额实例
         */
        @JvmStatic
        fun of(value: Double): Money {
            return of(BigDecimal.valueOf(value))
        }

        /**
         * 通过指定金额值和上下文的配置进行构建
         * @param value 金额值
         * @return 金额实例
         */

        @JvmStatic
        fun of(value: BigDecimal): Money {
            // 上下文自定义的金额配置
            val config = MoneyConfigHolder.get()
            if (config != null) {
                return of(value, config)
            }
            return of(value, MoneyConfig.DEFAULT)
        }

        /**
         * 通过指定金额值和金额配置进行校验
         * @param value  金额值
         * @param config 金额配置
         * @return 金额实例
         */
        @JvmStatic
        fun of(value: BigDecimal, config: MoneyConfig): Money {
            return of(value, config.decimalLimit, config.decimalType, config.quantileLimit, config.quantileSymbol)
        }

        /**
         * 通过金额值和具体配置进行构建
         * 会进行参数校验
         * @param value         金额值
         * @param decimalLimit  小数位限制数量
         * @param decimalType   小数位处理方案
         * @param quantileLimit 分位间隔数量
         * @return 金额实例
         */
        @JvmStatic
        fun of(
            value: BigDecimal, decimalLimit: Int, decimalType: RoundingMode?, quantileLimit: Int?,
            quantileSymbol: String?
        ): Money {
            var decimalType = decimalType
            // 小数位配置, 如果指定了小数位数量, 但是未指定小数位处理方案, 则使用默认的方案
            decimalType = if (MoneyConfig.validDecimal(decimalLimit)) {
                decimalType ?: DEFAULT_DECIMAL_TYPE
            } else {
                RoundingMode.DOWN
            }
            // 分位配置
            if (MoneyConfig.validQuantile(quantileLimit)) {
                // 有效分位则进行校验
                MoneyConfig.validQuantile(quantileLimit, quantileSymbol)
            }
            return ofPrivate(value, decimalLimit, decimalType, quantileLimit, quantileSymbol)
        }

        /**
         * 内部用 - 通过金额值和具体配置进行构建
         * 不进行参数校验
         * @param value         金额值
         * @param decimalLimit  小数位限制数量
         * @param decimalType   小数位处理方案
         * @param quantileLimit 分位间隔数量
         * @return 金额实例
         */
        private fun ofPrivate(
            value: BigDecimal, decimalLimit: Int, decimalType: RoundingMode,
            quantileLimit: Int?, quantileSymbol: String?
        ): Money {
            return Money(
                decimalLimit, decimalType, quantileLimit, quantileSymbol,
                value.setScale(decimalLimit, decimalType)
            )
        }
    }
}
