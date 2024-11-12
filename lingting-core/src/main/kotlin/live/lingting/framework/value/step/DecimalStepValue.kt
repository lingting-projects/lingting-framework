package live.lingting.framework.value.step

import live.lingting.framework.value.StepValue
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

/**
 * @author lingting 2024-02-27 14:15
 */
class DecimalStepValue(start: BigDecimal, step: BigDecimal, maxIndex: BigInteger?, maxValue: BigDecimal?) : AbstractConcurrentStepValue<BigDecimal>() {
    /**
     * 与初始值
     */
    protected val start: BigDecimal

    /**
     * 每次步进值
     */
    protected val step: BigDecimal

    /**
     * 最大步进次数, 为null表示无限步进次数
     */
    protected val maxIndex: BigInteger?

    /**
     * 最大值(可以等于), 为null表示无最大值限制
     */
    protected val maxValue: BigDecimal?

    constructor(step: BigDecimal, maxIndex: BigInteger?, maxValue: BigDecimal?) : this(BigDecimal.ZERO, step, maxIndex, maxValue)

    init {
        require(!(start == null || step == null)) { String.format("Invalid start value[%s] or step value[%s].", start, step) }
        this.start = start
        this.step = step
        this.maxIndex = maxIndex
        this.maxValue = maxValue
    }

    fun start(start: BigDecimal): DecimalStepValue {
        return DecimalStepValue(start, step, maxIndex, maxValue)
    }

    override fun firt(): BigDecimal {
        return start
    }

    override fun copy(): StepValue<BigDecimal?> {
        return start(start)
    }

    override fun doHasNext(): Boolean {
        // 当前索引位置大于等于最大索引位置则不可以继续步进
        if (maxIndex != null && index.compareTo(maxIndex) > -1) {
            return false
        }
        // 下一个值大于最大值则不可以继续步进
        if (maxValue != null) {
            val next = calculateNext()
            return next!!.compareTo(maxValue) < 1
        }
        return true
    }

    override fun doCalculate(index: BigInteger): BigDecimal {
        val decimal = BigDecimal(index)
        // 步进值 * 索引位置 = 增长值
        val growth = step.multiply(decimal, MathContext.UNLIMITED)
        // 与初始值相加
        return start.add(growth)
    }
}
