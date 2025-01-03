package live.lingting.framework.api

import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2023-12-29 11:32
 */
class ScrollCursor<T, S>(
    private var function: ThrowingFunction<S, ScrollResult<T, S>>,
    private var scrollId: S?,
    data: List<T>,
) : CursorValue<T>() {

    init {
        // 初始数据就为空, 直接结束
        if (data.isEmpty()) {
            empty = true
        } else {
            current.addAll(data)
        }
    }

    override fun nextBatchData(): List<T> {
        val result = function.apply(scrollId)
        scrollId = result.cursor
        return result.records
    }
}
