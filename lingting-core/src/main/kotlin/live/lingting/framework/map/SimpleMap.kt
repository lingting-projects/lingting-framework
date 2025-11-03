package live.lingting.framework.map

import live.lingting.framework.util.BooleanUtils.isFalse
import live.lingting.framework.util.BooleanUtils.isTrue
import live.lingting.framework.util.OptionalUtils.optional
import java.util.Optional
import java.util.function.Function
import java.util.function.Predicate

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

    // region bool

    fun getBool(key: K): Boolean? {
        val v = find(key)
        if (v.isTrue()) {
            return true
        }
        if (v.isFalse()) {
            return false
        }
        return null
    }

    fun getBool(key: K, default: Boolean) = getBool(key) ?: default

    fun isTrue(key: K) = getBool(key, false)
    fun isFalse(key: K) = !getBool(key, true)

    // endregion

}
