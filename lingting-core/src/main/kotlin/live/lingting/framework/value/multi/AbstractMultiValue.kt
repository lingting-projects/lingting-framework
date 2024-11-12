package live.lingting.framework.value.multi

import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.value.MultiValue
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author lingting 2024-09-05 20:33
 */
abstract class AbstractMultiValue<K, V, C : MutableCollection<V>> protected constructor(
    protected val allowModify: Boolean,
    protected val supplier: Supplier<C>
) : MultiValue<K, V, C> {
    protected val map: MutableMap<K, C> = ConcurrentHashMap()

    protected constructor(supplier: Supplier<C>) : this(true, supplier)

    protected open fun convert(key: K): K {
        return key
    }

    protected fun absent(key: K): C {
        var key = key
        if (!allowModify && !hasKey(key)) {
            throw UnsupportedOperationException()
        }
        key = convert(key)
        return map.computeIfAbsent(key) { k: K -> supplier.get() }
    }

    override fun ifAbsent(key: K) {
        absent(key)
    }

    // region fill
    override fun add(key: K) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        absent(key)
    }

    override fun add(key: K, value: V) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        absent(key).add(value)
    }

    override fun addAll(key: K, values: Collection<V>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        absent(key).addAll(values)
    }

    override fun addAll(key: K, values: Iterable<V>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        val c = absent(key)
        values.forEach(Consumer<V> { e: V -> c.add(e) })
    }

    override fun addAll(map: Map<K, Collection<V>>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        map.forEach { (key: K, values) -> this.addAll(key, values) }
    }

    override fun addAll(value: MultiValue<K, V, C>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        value.forEach((BiConsumer { key: K, values: C -> this.addAll(key, values) }))
    }

    override fun put(key: K, value: V) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        putAll(key, listOf<V>(value))
    }

    override fun putAll(key: K, values: Iterable<V>) {
        var key = key
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        val c = supplier.get()
        values.forEach(Consumer<V> { e: V -> c.add(e) })
        key = convert(key)
        map.put(key, c)
    }

    override fun putAll(map: Map<K, Collection<V>>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        map.forEach { (key: K, values) -> this.putAll(key, values) }
    }

    override fun putAll(value: MultiValue<K, V, C>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        value.forEach((BiConsumer { key: K, values: C -> this.putAll(key, values) }))
    }

    override fun replace(oldKey: K, newKey: K) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        val c = map.remove(oldKey)
        if (c != null) {
            map.put(newKey, c)
        }
    }

    protected fun from(value: MultiValue<K, V, out Collection<V>>) {
        from(value) { vs: Collection<V> ->
            val c = supplier.get()
            c.addAll(vs)
            c
        }
    }

    protected fun <S : Collection<V>> from(value: MultiValue<K, V, S>, function: Function<S, C>) {
        value.forEach((BiConsumer { k: K, vs: S ->
            val rk = convert(k)
            val rv = function.apply(vs)
            map.put(rk, rv)
        }))
    }

    override val isEmpty: Boolean
        // endregion
        get() = map.isEmpty()

    override fun isEmpty(key: K): Boolean {
        return isEmpty || !hasKey(key) || absent(key)!!.isEmpty()
    }

    override fun size(): Int {
        return map.size
    }

    override fun hasKey(key: K): Boolean {
        var key = key
        key = convert(key)
        return map.containsKey(key)
    }

    override fun get(key: K): C {
        if (!allowModify && !hasKey(key)) {
            return supplier.get()
        }
        return absent(key)
    }

    override fun iterator(key: K): Iterator<V> {
        return get(key)!!.iterator()
    }

    override fun first(key: K): V {
        val collection: Collection<V> = get(key)
        if (CollectionUtils.isEmpty(collection)) {
            return null
        }
        return collection!!.iterator().next()
    }

    override fun keys(): Set<K> {
        return map.keys
    }

    override fun values(): Collection<C> {
        return map.values
    }

    override fun map(): Map<K, C> {
        val hashMap: MutableMap<K, C> = HashMap()
        map.forEach { (k: K, vs: C) ->
            val c = supplier.get()
            c.addAll(vs)
            hashMap.put(k, c)
        }
        return hashMap
    }

    override fun entries(): Set<Map.Entry<K, C>> {
        return map.entries
    }

    override fun unmodifiable(): MultiValue<K, V, Collection<V>> {
        return UnmodifiableMultiValue(this)
    }

    // endregion
    // region remove
    override fun clear() {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        map.clear()
    }

    override fun remove(key: K): C {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        absent(key)
        return map.remove(key)
    }

    override fun remove(key: K, value: V): Boolean {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        if (!hasKey(key)) {
            return false
        }
        val absent = absent(key)
        return absent.remove(value)
    }

    // endregion
    // region function
    override fun forEach(consumer: BiConsumer<K, C>) {
        map.forEach(consumer!!)
    }

    override fun each(consumer: BiConsumer<K, V>) {
        forEach { k: K, c: C -> c!!.forEach { v: V -> consumer.accept(k, v) } }
    } // endregion
}
