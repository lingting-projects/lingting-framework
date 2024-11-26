package live.lingting.framework.thread

import java.util.Stack
import java.util.function.Supplier

/**
 * @author lingting 2024-03-29 13:30
 */
class StackThreadLocal<T> {
    protected val local: ThreadLocal<Stack<T>> = ThreadLocal.withInitial<Stack<T>>(Supplier<Stack<T>> { Stack() })

    fun put(t: T) {
        val stack = local.get()
        stack.push(t)
    }

    fun get(): T? {
        val stack = local.get()
        if (stack.empty()) {
            return null
        }
        return stack.peek()
    }

    fun pop(): T? {
        val stack = local.get()
        if (stack.empty()) {
            return null
        }
        return stack.pop()
    }
}
