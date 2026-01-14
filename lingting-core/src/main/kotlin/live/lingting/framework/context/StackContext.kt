package live.lingting.framework.context

import java.util.Stack
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

/**
 * @author lingting 2024-03-29 13:30
 */
@Suppress("UNCHECKED_CAST")
class StackContext<T> : Context<Stack<T>> {

    companion object {

        private val defaultInit = Supplier<Stack<*>> { Stack<Any>() }

    }

    constructor(
        init: Supplier<out Stack<T>>? = defaultInit as Supplier<Stack<T>>,
        from: ConcurrentHashMap<ContextSource, Stack<T>?> = ConcurrentHashMap(),
    ) : super(init, from)

    fun peek(): T? {
        return operateIfNotNull {
            if (it.isEmpty()) null else it.peek()
        }
    }

    fun push(t: T) {
        operateRequire("stack is required") { it.push(t) }
    }

    fun pop(): T? {
        return operateIfNotNull {
            if (it.isEmpty()) null else it.pop()
        }
    }

}
