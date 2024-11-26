package live.lingting.framework.value.multi

import java.util.Collections

/**
 * @author lingting 2024-09-05 21:07
 */
class UnmodifiableMultiValue<K, V>(value: AbstractMultiValue<K, V, *>) :
    AbstractMultiValue<K, V, MutableCollection<V>>(false, { mutableListOf<V>() }) {
    init {
        from(value) { Collections.unmodifiableCollection<V>(it) }
    }
}
