package live.lingting.framework.value

import live.lingting.framework.value.cursor.BatchCursorValue

/**
 * @author lingting 2024/11/25 16:44
 */
interface UniqueValue<T> {

    fun next(): T

    fun batch(count: Int): List<T>

    fun cursor(count: Int): CursorValue<T> {
        return cursor(count, 100)
    }

    /**
     * @param count 获取的唯一值数量
     * @param size 每次获取最大数量
     */
    fun cursor(count: Int, size: Int): CursorValue<T> {
        return BatchCursorValue(::batch, count.toLong(), size)
    }

}
