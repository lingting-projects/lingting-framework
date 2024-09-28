package live.lingting.framework.elasticsearch.datascope;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lingting 2023-06-27 11:06
 */
@RequiredArgsConstructor
public class DefaultElasticsearchDataPermissionHandler implements ElasticsearchDataPermissionHandler {

	protected final List<ElasticsearchDataScope> scopes;

	@Override
	public List<ElasticsearchDataScope> dataScopes() {
		return scopes == null ? Collections.emptyList() : Collections.unmodifiableList(scopes);
	}

	@Override
	public List<ElasticsearchDataScope> filterDataScopes(String index) {
		if (scopes == null) {
			return Collections.emptyList();
		}
		return dataScopes().stream().filter(scope -> scope.includes(index)).collect(Collectors.toList());
	}

	@Override
	public boolean ignorePermissionControl(String index) {
		return false;
	}

}
