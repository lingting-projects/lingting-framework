package live.lingting.framework.value.multi

import java.util.function.Supplier

/**
 * @author lingting 2024-09-05 20:28
 */
class ListMultiValue<K, V> : AbstractMultiValue<K, V, MutableList<V>> {

    constructor() : super({ ArrayList<V>() })

    constructor(supplier: Supplier<MutableList<V>>) : super(supplier)

}
