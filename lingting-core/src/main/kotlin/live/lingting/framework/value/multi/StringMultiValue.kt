package live.lingting.framework.value.multi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-14 11:21
 */
public class StringMultiValue extends AbstractMultiValue<String, String, Collection<String>> {

	public StringMultiValue() {
		this(ArrayList::new);
	}

	protected StringMultiValue(Supplier<Collection<String>> supplier) {
		super(supplier);
	}

	protected StringMultiValue(boolean allowModify, Supplier<Collection<String>> supplier) {
		super(allowModify, supplier);
	}

	@Override
	public StringMultiValue unmodifiable() {
		StringMultiValue value = new StringMultiValue(false, supplier);
		value.from(this, Collections::unmodifiableCollection);
		return value;
	}

}
