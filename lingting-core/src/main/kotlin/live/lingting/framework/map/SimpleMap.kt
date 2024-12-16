package live.lingting.framework.map

import java.util.Optional
import java.util.function.Function
import java.util.function.Predicate
import live.lingting.framework.util.OptionalUtils.optional

/**
 * @author lingting 2024-04-20 16:17
 */
interface SimpleMap<K, V> : MutableMap<K, V> {

    fun find(key: K): V? {
        return if (isEmpty()) null else get(key)
    }

    fun <T> find(key: K, func: Function<Optional<V>, T>): T {
        val v = find(key)
        val optional = v.optional()
        return func.apply(optional)
    }

    fun <T> find(key: K, defaultValue: T, func: Function<V, T>): T {
        return find(key, defaultValue, { it == null }, func)
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

}
