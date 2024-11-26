package live.lingting.framework.api

import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2023-12-29 11:32
 */
class LimitCursor<T>(private val limit: ThrowingFunction<Long, PaginationResult<T>>) : CursorValue<T>() {
    private var index: Long = 1

    override fun nextBatchData(): List<T> {
        val result = limit.apply(index)
        index++
        return result.records
    }
}
