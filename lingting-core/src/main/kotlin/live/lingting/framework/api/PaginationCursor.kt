package live.lingting.framework.api

import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2023-12-29 11:32
 */
class PaginationCursor<T>(
    params: PaginationParams,
    private val function: ThrowingFunction<PaginationParams, PaginationResult<T>>,
) : CursorValue<T>() {
    private var page = params.page
    private val size = params.size
    private val sorts = params.sorts

    override fun nextBatchData(): List<T> {
        val params = PaginationParams(page, size, sorts)
        val result = function.apply(params)
        page++
        return result.records
    }
}
