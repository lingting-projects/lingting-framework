package live.lingting.framework.elasticsearch.datascope;

import java.util.Collections;
import java.util.List;

/**
 * @author lingting 2023-06-27 11:06
 */
public class DefaultElasticsearchDataPermissionHandler implements ElasticsearchDataPermissionHandler {

	protected final List<ElasticsearchDataScope> scopes;

	public DefaultElasticsearchDataPermissionHandler(List<ElasticsearchDataScope> scopes) {
		this.scopes = scopes;
	}

	@Override
	public List<ElasticsearchDataScope> dataScopes() {
		return scopes == null ? Collections.emptyList() : Collections.unmodifiableList(scopes);
	}

	@Override
	public List<ElasticsearchDataScope> filterDataScopes(String index) {
		if (scopes == null) {
			return Collections.emptyList();
		}
		return dataScopes().stream().filter(scope -> scope.includes(index)).toList();
	}

	@Override
	public boolean ignorePermissionControl(String index) {
		return false;
	}

}
