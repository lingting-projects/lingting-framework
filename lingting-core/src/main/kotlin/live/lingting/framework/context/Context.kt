package live.lingting.framework.context

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import java.util.function.Supplier

/**
 * 用于方便切换上下文管理实现, 避免切换jdk导致大量的修改代码
 * <p>替换 ThreadLocal 和 CarrierThreadLocal</p>
 * @author lingting 2024/11/30 18:38
 */
@Suppress("UNCHECKED_CAST")
open class Context<T> {

    companion object {

        @JvmStatic
        var creator = Function<Supplier<*>?, ThreadLocal<*>> { ThreadLocal<Any>() }

        private val FIRST = ConcurrentHashMap<Long, Boolean>()

    }

    @JvmOverloads
    constructor(
        init: Supplier<out T>? = null,
        local: ThreadLocal<T> = creator.apply(init) as ThreadLocal<T>,
    ) {
        this.local = local
        this.init = init
    }

    protected val local: ThreadLocal<T>

    protected val init: Supplier<out T>?

    protected open fun id(): Long = Thread.currentThread().threadId()

    @JvmOverloads
    open fun <E> operateRequire(message: String = "value must be not null", operate: Function<T, E?>): E? {
        val v = get()
        requireNotNull(v) { message }
        return operate.apply(v)
    }

    open fun <E> operate(operate: Function<T, E?>): E? {
        val v = get()
        if (isNull(v)) {
            return null
        }
        return operate.apply(v!!)
    }

    protected open fun isNull(t: T?): Boolean {
        return t == null
    }

    open fun get(): T? {
        var t = local.get()

        if (t != null) {
            return t
        }

        if (init == null) {
            return null
        }

        val id = id()

        val isFirst = FIRST.compute(id) { _, v ->
            return@compute v == null || v == false
        }
        if (isFirst != true) {
            return null
        }
        val v = init.get()
        local.set(v)
        return v
    }

    open fun set(t: T?) {
        local.set(t)
    }

    open fun remove() {
        val id = id()
        FIRST[id] = false
        local.remove()
    }

}
