package live.lingting.framework.value.step

import java.math.BigInteger
import live.lingting.framework.value.StepValue

/**
 * @author lingting 2024-02-27 15:44
 */
class LongStepValue(
    /**
     * 与初始值
     */
    protected val start: Long,
    /**
     * 每次步进值
     */
    protected val step: Long,
    /**
     * 最大步进次数, 为null表示无限步进次数
     */
    protected val maxIndex: Long?,
    /**
     * 最大值(可以等于), 为null表示无最大值限制
     */
    protected val maxValue: Long?,
) : AbstractConcurrentStepValue<Long>() {

    constructor(step: Long, maxIndex: Long?, maxValue: Long?) : this(0, step, maxIndex, maxValue)

    fun start(start: Long): LongStepValue {
        return LongStepValue(start, step, maxIndex, maxValue)
    }

    override fun first(): Long {
        return start
    }

    override fun copy(): StepValue<Long> {
        return start(start)
    }

    override fun doHasNext(): Boolean {
        // 当前索引位置大于等于最大索引位置则不可以继续步进
        if (maxIndex != null && index.toLong() >= maxIndex) {
            return false
        }
        // 下一个值大于最大值则不可以继续步进
        if (maxValue != null) {
            val next = calculateNext()
            return next.compareTo(maxValue) < 1
        }
        return true
    }

    override fun doCalculate(index: BigInteger): Long {
        val decimal = index.toLong()
        // 步进值 * 索引位置 = 增长值
        val growth = step * decimal
        // 与初始值相加
        return start + growth
    }
}
