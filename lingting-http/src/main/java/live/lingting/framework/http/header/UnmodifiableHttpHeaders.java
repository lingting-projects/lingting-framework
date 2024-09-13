package live.lingting.framework.http.header;

import live.lingting.framework.value.multi.AbstractMultiValue;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author lingting 2024-09-12 23:45
 */
public class UnmodifiableHttpHeaders extends AbstractHttpHeaders implements HttpHeaders {

	public UnmodifiableHttpHeaders(AbstractMultiValue<String, String, ?> value) {
		super(false, ArrayList::new);
		value.forEach(((k, vs) -> map.put(k, Collections.unmodifiableCollection(vs))));
	}

	@Override
	public UnmodifiableHttpHeaders unmodifiable() {
		return this;
	}

}
