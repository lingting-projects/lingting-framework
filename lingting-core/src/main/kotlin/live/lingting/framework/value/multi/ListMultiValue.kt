package live.lingting.framework.value.multi

import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Supplier

/**
 * @author lingting 2024-09-05 20:28
 */
class ListMultiValue<K, V> constructor(supplier: Supplier<List<V>> = Supplier { CopyOnWriteArrayList() }) : AbstractMultiValue<K, V, List<V>?>(supplier)
