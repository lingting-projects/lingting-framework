package live.lingting.framework.value.multi;

import java.util.Collection;
import java.util.Collections;

/**
 * @author lingting 2024-09-05 21:07
 */
public class UnmodifiableMultiValue<K, V> extends AbstractMultiValue<K, V, Collection<V>> {

	public UnmodifiableMultiValue(AbstractMultiValue<K, V, ?> value) {
		super(false, Collections::emptyList);
		from(value, Collections::unmodifiableCollection);
	}

}
