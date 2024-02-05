package live.lingting.framework.mybatis.typehandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lingting 2022/12/19 16:27
 */
public abstract class AbstractMapTypeHandler<K, V> extends AbstractJacksonTypeHandler<Map<K, V>> {

	@Override
	protected Map<K, V> defaultValue() {
		return new HashMap<>();
	}

	@Override
	protected String defaultJson() {
		return "{}";
	}

}
