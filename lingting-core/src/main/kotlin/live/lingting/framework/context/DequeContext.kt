package live.lingting.framework.context

import java.util.ArrayDeque
import java.util.Deque
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/**
 * @author lingting 2024-03-29 13:30
 */
@Suppress("UNCHECKED_CAST")
class DequeContext<T> : Context<Deque<T>> {

    companion object {

        private val defaultInit = Supplier<Deque<*>> { ArrayDeque<Any>() }

    }

    constructor(
        init: Supplier<out Deque<T>>? = defaultInit as Supplier<Deque<T>>,
        from: ConcurrentHashMap<ContextSource, Deque<T>?> = ConcurrentHashMap(),
    ) : super(init, from)

    /**
     * 获取第一个元素
     */
    fun peek(): T? {
        return operateIfNotNull {
            if (it.isEmpty()) null else it.peek()
        }
    }

    /**
     * 元素放入到队列的第一位
     */
    fun push(t: T) {
        operateRequire("deque is required") { it.push(t) }
    }

    /**
     * 弹出第一个元素
     */
    fun pop(): T? {
        return operateIfNotNull {
            if (it.isEmpty()) null else it.pop()
        }
    }

    /**
     * 获取最后一个元素
     */
    fun peekLast(): T? {
        return operateIfNotNull {
            if (it.isEmpty()) null else it.peekLast()
        }
    }

    /**
     * 元素放入到队列的最后一位
     */
    fun offerLast(t: T) {
        operateRequire("deque is required") { it.offerLast(t) }
    }

    /**
     * 弹出最后一个元素
     */
    fun pollLast(): T? {
        return operateIfNotNull {
            if (it.isEmpty()) null else it.pollLast()
        }
    }

}
