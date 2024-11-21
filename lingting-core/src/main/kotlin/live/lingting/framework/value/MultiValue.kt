package live.lingting.framework.value

import java.util.function.BiConsumer

/**
 * @author lingting 2024-09-05 21:17
 */
interface MultiValue<K, V, C : Collection<V>> {
    // region fill
    /**
     * 为指定key创建一个槽位(如果不存在)
     */
    fun ifAbsent(key: K)

    fun add(key: K)

    fun add(key: K, value: V)

    fun addAll(key: K, values: Collection<V>)

    fun addAll(key: K, values: Iterable<V>)

    fun addAll(map: Map<K, Collection<V>>)

    fun addAll(value: MultiValue<K, V, C>)

    fun put(key: K, value: V)

    fun putAll(key: K, values: Iterable<V>)

    fun putAll(map: Map<K, Collection<V>>)

    fun putAll(value: MultiValue<K, V, C>)

    fun replace(oldKey: K, newKey: K)

    // endregion
    // region get

    val isEmpty: Boolean

    fun isEmpty(key: K): Boolean

    fun size(): Int

    fun hasKey(key: K): Boolean

    fun get(key: K): C

    fun iterator(key: K): Iterator<V>

    fun first(key: K): V?

    fun first(key: K, defaultValue: V): V {
        val v = first(key)
        return v ?: defaultValue
    }

    fun keys(): Set<K>

    fun values(): Collection<C>

    fun map(): Map<K, C>

    fun entries(): Set<Map.Entry<K, C>>

    fun unmodifiable(): MultiValue<K, V, out Collection<V>>

    // endregion
    // region remove
    fun clear()

    fun remove(key: K): C?

    fun remove(key: K, value: V): Boolean

    // endregion
    // region function
    fun forEach(consumer: BiConsumer<K, C>)

    fun each(consumer: BiConsumer<K, V>)

    fun forEachSorted(consumer: BiConsumer<K, C>) {
        keys().stream().sorted().forEach { key -> consumer.accept(key, get(key)) }
    }

    fun forEachSorted(consumer: BiConsumer<K, C>, comparator: Comparator<K>) {
        keys().stream().sorted(comparator).forEach { key -> consumer.accept(key, get(key)) }
    }

    fun eachSorted(consumer: BiConsumer<K, V>) {
        forEachSorted { k, c -> c.forEach { v -> consumer.accept(k, v) } }
    }

    fun eachSorted(consumer: BiConsumer<K, V>, comparator: Comparator<K>) {
        forEachSorted({ k, c -> c.forEach { v -> consumer.accept(k, v) } }, comparator)
    } // endregion
}
