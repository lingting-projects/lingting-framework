package live.lingting.framework.value.cursor

import live.lingting.framework.value.CursorValue
import java.util.function.Function

/**
 * @author lingting 2025/10/28 11:14
 */
open class BatchCursorValue<T> @JvmOverloads constructor(
    val getFunc: Function<Int, List<T>>,
    /**
     * 最大获取数量
     */
    val maxSize: Long? = null,
    /**
     * 一次最多数量
     */
    val batchSize: Int = 100
) : CursorValue<T>() {

    /**
     * 剩余需要获取的数据
     */
    protected var remain: Long = maxSize ?: 0;

    override fun nextBatchData(): List<T> {
        // 无限获取
        if (maxSize == null || maxSize < 1) {
            return getFunc.apply(batchSize)
        }
        if (remain < 1) {
            return emptyList()
        }
        // 本次获取数量
        val size = remain.toInt().coerceAtMost(batchSize)
        return getFunc.apply(size)
    }
}
