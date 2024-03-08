package live.lingting.framework.elasticsearch.composer;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import live.lingting.framework.elasticsearch.ElasticSearchFunction;
import lombok.experimental.UtilityClass;

import static live.lingting.framework.elasticsearch.ElasticSearchUtils.fieldName;

/**
 * @author lingting 2024-03-06 17:44
 */
@UtilityClass
public class SortComposer {

	public static SortOptions sort(String field, Boolean desc) {
		return sort(field, Boolean.TRUE.equals(desc) ? SortOrder.Desc : SortOrder.Asc);
	}

	public static SortOptions sort(String field, SortOrder order) {
		return SortOptions.of(so -> so.field(fs -> fs.field(field).order(order)));
	}

	public static SortOptions desc(String field) {
		return sort(field, SortOrder.Desc);
	}

	public static SortOptions asc(String field) {
		return sort(field, SortOrder.Asc);
	}

	public static <E, T> SortOptions sort(ElasticSearchFunction<E, T> func, Boolean desc) {
		return sort(func, Boolean.TRUE.equals(desc) ? SortOrder.Desc : SortOrder.Asc);
	}

	public static <E, T> SortOptions sort(ElasticSearchFunction<E, T> func, SortOrder order) {
		String field = fieldName(func);
		return SortOptions.of(so -> so.field(fs -> fs.field(field).order(order)));
	}

	public static <E, T> SortOptions desc(ElasticSearchFunction<E, T> func) {
		return sort(func, SortOrder.Desc);
	}

	public static <E, T> SortOptions asc(ElasticSearchFunction<E, T> func) {
		return sort(func, SortOrder.Asc);
	}

}
