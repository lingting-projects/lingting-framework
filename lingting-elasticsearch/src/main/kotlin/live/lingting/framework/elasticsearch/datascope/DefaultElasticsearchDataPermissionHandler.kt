package live.lingting.framework.elasticsearch.datascope

import java.util.Collections


/**
 * @author lingting 2023-06-27 11:06
 */
open class DefaultElasticsearchDataPermissionHandler(protected val scopes: List<ElasticsearchDataScope>) : ElasticsearchDataPermissionHandler {
    override fun dataScopes(): List<ElasticsearchDataScope> {
        return Collections.unmodifiableList(scopes)
    }

    override fun filterDataScopes(index: String): List<ElasticsearchDataScope> {
        return dataScopes().filter { scope -> scope.includes(index) }
    }

    override fun ignorePermissionControl(index: String): Boolean {
        return false
    }
}
