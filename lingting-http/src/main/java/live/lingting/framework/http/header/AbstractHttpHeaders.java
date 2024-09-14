package live.lingting.framework.http.header;

import live.lingting.framework.value.multi.StringMultiValue;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-13 11:15
 */
public abstract class AbstractHttpHeaders extends StringMultiValue implements HttpHeaders {

	protected AbstractHttpHeaders(Supplier<Collection<String>> supplier) {
		super(supplier);
	}

	protected AbstractHttpHeaders(boolean allowModify, Supplier<Collection<String>> supplier) {
		super(allowModify, supplier);
	}

	@Override
	protected String convert(String key) {
		return key.toLowerCase();
	}

	@Override
	public UnmodifiableHttpHeaders unmodifiable() {
		return new UnmodifiableHttpHeaders(this);
	}

}
