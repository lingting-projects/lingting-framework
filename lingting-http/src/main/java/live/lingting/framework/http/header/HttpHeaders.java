package live.lingting.framework.http.header;

import live.lingting.framework.value.MultiValue;

import java.util.Collection;
import java.util.Map;

/**
 * @author lingting 2024-09-12 23:38
 */
public interface HttpHeaders extends MultiValue<String, String, Collection<String>> {

	static HttpHeaders empty() {
		return new CollectionHttpHeaders();
	}

	static HttpHeaders of(Map<String, ? extends Collection<String>> map) {
		HttpHeaders empty = empty();
		empty.addAll(map);
		return empty;
	}

	@Override
	UnmodifiableHttpHeaders unmodifiable();

	// region get

	default String contentType() {
		return first("Content-Type");
	}

	default Long contentLength() {
		String first = first("Content-Length", "0");
		return Long.parseLong(first);
	}

	// endregion

	// region set

	default HttpHeaders contentType(String contentType) {
		put("Content-Type", contentType);
		return this;
	}

	default HttpHeaders contentLength(long contentLength) {
		put("Content-Length", String.valueOf(contentLength));
		return this;
	}

	default HttpHeaders range(long start, long end) {
		put("Range", String.format("bytes=%d-%d", start, end));
		return this;
	}

	// endregion

}
