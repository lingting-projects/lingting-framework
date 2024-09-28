package live.lingting.framework.elasticsearch.datascope;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2024-07-01 17:29
 */
public class AllowElasticsearchDataPermissionHandler implements ElasticsearchDataPermissionHandler {

	@Override
	public List<ElasticsearchDataScope> dataScopes() {
		return new ArrayList<>();
	}

	@Override
	public List<ElasticsearchDataScope> filterDataScopes(String index) {
		return new ArrayList<>();
	}

	@Override
	public boolean ignorePermissionControl(String index) {
		return true;
	}

}
