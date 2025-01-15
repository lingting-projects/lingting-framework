package live.lingting.framework.value.cursor

import java.util.function.Function
import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2025/1/15 10:20
 */
class FunctionCursorValue<R, T>(val initRequest: R, val function: Function<R, Item<R, T>>) : CursorValue<T>() {

    var request: R? = initRequest
        private set

    override fun nextBatchData(): List<T> {
        return request.let {
            if (it == null) {
                emptyList()
            } else {
                val item = function.apply(it)
                request = item.next
                item.data ?: emptyList()
            }
        }
    }


    data class Item<R, T>(val next: R?, val data: List<T>?)
}
