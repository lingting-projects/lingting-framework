package live.lingting.framework.value.cursor

import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2024/11/25 16:47
 */
class CollectionCursorValue<T>(val collection: Collection<T>) : CursorValue<T>() {
    override fun nextBatchData(): List<T> {
        if (count > 0) {
            return emptyList()
        }
        if (collection is List) {
            return collection
        }
        return collection.toList()
    }

}
