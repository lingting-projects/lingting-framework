package live.lingting.framework.elasticsearch.composer;

import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import live.lingting.framework.elasticsearch.ElasticsearchFunction;
import live.lingting.framework.elasticsearch.ElasticsearchUtils;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

import static live.lingting.framework.elasticsearch.ElasticsearchUtils.fieldName;

/**
 * @author lingting 2024-03-06 17:45
 */
@UtilityClass
public class SourceComposer {

	public static SourceConfig includes(String value, String... values) {
		return SourceConfig.of(sc -> sc.filter(sf -> sf.includes(value, values)));
	}

	@SafeVarargs
	public static <E> SourceConfig includes(ElasticsearchFunction<E, ?> function,
			ElasticsearchFunction<E, ?>... functions) {
		String value = fieldName(function);
		String[] values = Arrays.stream(functions).map(ElasticsearchUtils::fieldName).toArray(String[]::new);
		return includes(value, values);
	}

}
