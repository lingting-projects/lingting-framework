
package live.lingting.polaris.grpc.metadata;

import io.grpc.Context;
import io.grpc.Context.Key;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * copy from
 * https://github.com/Tencent/spring-cloud-tencent/blob/main/spring-cloud-tencent-commons/src/main/java/com/tencent/cloud/common/metadata/MetadataContext.java
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class MetadataContext {

	public static final Key<MetadataContext> METADATA_CONTEXT_KEY = Context.keyWithDefault("MetadataContext",
		new MetadataContext());

	public static final String FRAGMENT_HEADER = "header";

	public static final String FRAGMENT_GRPC_CONTEXT = "grpc_context";

	private Map<String, Map<String, String>> fragmentContexts;

	public MetadataContext() {
		this.fragmentContexts = new ConcurrentHashMap<>();
	}

	public Map<String, String> getHeaderFragment() {
		return getFragment(FRAGMENT_HEADER);
	}

	public Map<String, String> getGrpcContextFragment() {
		return getFragment(FRAGMENT_GRPC_CONTEXT);
	}

	private Map<String, String> getFragment(final String fragment) {
		Map<String, String> fragmentContext = fragmentContexts.get(fragment);
		if (fragmentContext == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(fragmentContext);
	}

	public void putHeaderFragment(final String key, final String value) {
		putHeaderFragment(FRAGMENT_HEADER, key, value);
	}

	public void putContextFragment(final String key, final String value) {
		putHeaderFragment(FRAGMENT_GRPC_CONTEXT, key, value);
	}

	private void putHeaderFragment(final String fragment, final String key, final String value) {
		Map<String, String> fragmentContext = fragmentContexts.computeIfAbsent(fragment,
			k -> new ConcurrentHashMap<>());
		fragmentContext.put(key, value);
	}

	public void reset() {
		fragmentContexts = new ConcurrentHashMap<>();
	}

	@Override
	public String toString() {
		return "MetadataContext{" + "fragmentContexts=" + fragmentContexts + '}';
	}

}
