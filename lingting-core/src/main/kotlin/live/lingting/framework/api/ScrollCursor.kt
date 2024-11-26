package live.lingting.framework.api

import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2023-12-29 11:32
 */
class ScrollCursor<T, S>(
    private var scroll: ThrowingFunction<S, ScrollResult<T, S>>,
    private var scrollId: S?,
    private var data: List<T>,
) : CursorValue<T>() {

    init {
        // 初始数据就为空, 直接结束
        if (CollectionUtils.isEmpty(data)) {
            empty = true
        } else {
            current.addAll(data)
        }
    }

    override fun nextBatchData(): List<T> {
        val result = scroll.apply(scrollId)
        scrollId = result.cursor
        return result.records
    }
}
