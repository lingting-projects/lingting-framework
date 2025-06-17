package live.lingting.framework.api

import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2023-12-29 11:32
 */
class ScrollCursor<T, S>(
    params: ScrollParams<S>,
    private var function: ThrowingFunction<ScrollParams<S>, ScrollResult<T, S>>,
) : CursorValue<T>() {

    private var scrollId = params.cursor

    private val size = params.size

    override fun nextBatchData(): List<T> {
        val params = ScrollParams(size, scrollId)
        val result = function.apply(params)
        scrollId = result.cursor
        return result.records
    }

}
