package live.lingting.framework.value

import live.lingting.framework.value.cursor.CollectionCursorValue

/**
 * @author lingting 2024/11/25 16:44
 */
interface UniqueValue<T> {

    fun next(): T

    fun batch(count: Int): List<T>

    fun cursor(count: Int): CursorValue<T> {
        val batch = batch(count)
        return CollectionCursorValue(batch)
    }

}
