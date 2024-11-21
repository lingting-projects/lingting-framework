package live.lingting.framework.api

/**
 * @author lingting 2024-02-02 17:54
 */
data class ScrollResult<T, C>(
    var records: List<T>,
    var cursor: C?,
    var total: Long,
) {

    constructor() : this(emptyList(), null, 0)

    companion object {
        @JvmStatic
        fun <T, C> of(collection: List<T>, cursor: C?): ScrollResult<T, C> {
            return ScrollResult(collection, cursor, collection.size.toLong())
        }

        @JvmStatic
        fun <T, C> empty(): ScrollResult<T, C> {
            return ScrollResult<T, C>(emptyList<T>(), null, 0)
        }
    }
}
