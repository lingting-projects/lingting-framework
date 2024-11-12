package live.lingting.framework.value.multi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-05 20:28
 */
public class ListMultiValue<K, V> extends AbstractMultiValue<K, V, List<V>> {

	public ListMultiValue() {
		this(CopyOnWriteArrayList::new);
	}

	public ListMultiValue(Supplier<List<V>> supplier) {
		super(supplier);
	}

}
