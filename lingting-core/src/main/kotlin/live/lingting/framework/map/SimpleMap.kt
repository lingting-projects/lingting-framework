package live.lingting.framework.map

import live.lingting.framework.util.BooleanUtils
import java.util.*
import java.util.function.Function
import java.util.function.Predicate

/**
 * @author lingting 2024-04-20 16:17
 */
interface SimpleMap<K, V> : MutableMap<K, V> {
    fun find(key: K): V? {
        return if (isEmpty()) null else get(key)
    }

    fun <T> find(key: K, func: Function<Optional<V>?, T>): T {
        val v = find(key)
        val optional: Optional<V> = Optional.ofNullable(v)
        return func.apply(optional)
    }

    fun <T> find(key: K, defaultValue: T, func: Function<V, T>): T {
        return find(key, defaultValue, { obj: V -> Objects.isNull(obj) }, func)
    }

    /**
     * @param usingDefault 如果返回true表示使用默认值
     */
    fun <T> find(key: K, defaultValue: T, usingDefault: Predicate<V>, func: Function<V, T>): T {
        val v = find(key)
        if (v == null || usingDefault.test(v)) {
            return defaultValue
        }
        return func.apply(v)
    }

    fun toBoolean(key: K): Boolean {
        return find<Boolean>(key, false, Function<V, Boolean> { obj: V -> BooleanUtils.isTrue(obj) })
    }
}