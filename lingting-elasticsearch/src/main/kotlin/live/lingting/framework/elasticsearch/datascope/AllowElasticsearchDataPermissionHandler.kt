package live.lingting.framework.elasticsearch.datascope;

import java.util.List;

/**
 * @author lingting 2024-07-01 17:29
 */
public class AllowElasticsearchDataPermissionHandler implements ElasticsearchDataPermissionHandler {

	@Override
	public List<ElasticsearchDataScope> dataScopes() {
		return List.of();
	}

	@Override
	public List<ElasticsearchDataScope> filterDataScopes(String index) {
		return List.of();
	}

	@Override
	public boolean ignorePermissionControl(String index) {
		return true;
	}

}
