package live.lingting.framework.value

import live.lingting.framework.util.Slf4jUtils.logger
import java.util.LinkedList
import java.util.Spliterator
import java.util.Spliterators
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.streams.toList

/**
 * @author lingting 2023-12-29 11:30
 */
abstract class CursorValue<T> : Iterator<T> {

    protected val log = logger()

    protected val current = LinkedList<T>()

    /**
     * 是否已经无数据了, 如果为true, 则不会继续调用 [.nextBatchData]方法, 且
     * [.hasNext]方法永远返回false
     */
    protected var empty: Boolean = false

    /**
     * 已读取数据数量
     */
    var count: Long = 0
        protected set

    override fun hasNext(): Boolean {
        if (current.isNotEmpty()) {
            return true
        }

        if (empty) {
            return false
        }

        val list = nextBatchData()

        if (list.isEmpty()) {
            empty = true
            return false
        }

        current.addAll(list)
        return true
    }

    override fun next(): T {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        count++
        return current.removeFirst()
    }

    /**
     * 获取下一批数据
     */
    protected abstract fun nextBatchData(): List<T>

    fun stream(): Stream<T> {
        val spliterator = Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED)
        return StreamSupport.stream(spliterator, false)
    }

    fun toList(): List<T> {
        return stream().toList()
    }

}
