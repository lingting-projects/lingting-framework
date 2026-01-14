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

    @JvmOverloads
    constructor(
        init: Supplier<out T>? = null,
        from: ConcurrentHashMap<ContextSource, T?> = ConcurrentHashMap(),
    ) {
        this.map = from
        this.init = init
    }

    init {
        ContextClear.start(this)
    }

    protected val map: ConcurrentHashMap<ContextSource, T?>

    protected val init: Supplier<out T>?

    protected val first = ConcurrentHashMap<ContextSource, Boolean>()

    open fun source(): ContextSource = ThreadContextSource(Thread.currentThread())

    open fun size(): Int {
        return map.size
    }

    @JvmOverloads
    open fun <E> operateRequire(message: String = "value must be not null", operate: Function<T, E?>): E? {
        val v = get()
        requireNotNull(v) { message }
        return operate.apply(v)
    }

    open fun <E> operateIfNotNull(operate: Function<T, E?>): E? {
        val v = get() ?: return null
        return operate.apply(v)
    }

    open fun get(): T? {
        val source = source()
        val absent = map.computeIfAbsent(source) {
            val isFirst = first.compute(source) { _, v ->
                v == null || !v
            }
            if (isFirst == true && init != null) {
                init.get()
            } else {
                null
            }
        }

        return absent
    }

    open fun get(defaultValue: T): T {
        val v = get()
        if (v == null) {
            set(defaultValue)
            return defaultValue
        }
        return v
    }

    open fun set(t: T?) {
        map[source()] = t
    }

    open fun remove() {
        val source = source()
        remove(source)
    }

    open fun remove(source: ContextSource) {
        first.remove(source)
        map.remove(source)

    }

    /**
     * 清除已经失效线程的数据
     */
    open fun clear() {
        val set = mutableSetOf<ContextSource>()
        map.keys.forEach {
            if (!it.isAlive()) {
                set.add(it)
            }
        }
        first.keys.forEach {
            if (!it.isAlive()) {
                set.add(it)
            }
        }
        set.forEach {
            remove(it)
        }
    }

}
