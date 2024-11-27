package live.lingting.framework.queue

import live.lingting.framework.lock.JavaReentrantLock

/**
 * 循环队列
 * @author lingting 2023-05-30 10:25
 */
class CircularQueue<T> {
    private val lock = JavaReentrantLock()

    private val source: MutableList<T> = ArrayList()

    private var iterator: Iterator<T>? = null

    fun add(t: T): CircularQueue<T> {
        source.add(t)
        return this
    }

    fun addAll(collection: Collection<T>): CircularQueue<T> {
        source.addAll(collection)
        return this
    }

    fun pool(): T? {
        return lock.getByInterruptibly<T?> {
            if (source.isNullOrEmpty()) {
                return@getByInterruptibly null
            }
            if (iterator == null || !iterator!!.hasNext()) {
                iterator = source.iterator()
            }
            iterator!!.next()
        }
    }
}
