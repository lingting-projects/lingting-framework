package live.lingting.framework.value.multi


import java.util.function.Supplier

/**
 * @author lingting 2024-09-05 21:07
 */
class UnmodifiableMultiValue<K, V>(value: AbstractMultiValue<K?, V?, *>) : AbstractMultiValue<K, V, Collection<V>?>(false, Supplier<Collection<V>> { emptyList() }) {
    init {
        from(value) { c: Collection<T?>? -> Collections.unmodifiableCollection(c) }
    }
}
