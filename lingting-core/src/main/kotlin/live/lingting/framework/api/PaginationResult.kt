package live.lingting.framework.api

import java.util.function.Consumer
import java.util.function.Function

/**
 * @author lingting 2024-02-02 17:53
 */
data class PaginationResult<T> @JvmOverloads constructor(
    var total: Long = 0,
    var records: List<T> = emptyList(),
) {

    constructor(records: List<T>) : this(records.size.toLong(), records)

    fun <V : Any> map(function: Function<T, V>): PaginationResult<V> {
        return PaginationResult(total, records.map { function.apply(it) })
    }

    fun forEach(consumer: Consumer<T>) {
        records.forEach(consumer)
    }

}
