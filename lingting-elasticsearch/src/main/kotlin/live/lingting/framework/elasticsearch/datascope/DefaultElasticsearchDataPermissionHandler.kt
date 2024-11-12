package live.lingting.framework.elasticsearch.datascope

import java.util.*

/**
 * @author lingting 2023-06-27 11:06
 */
open class DefaultElasticsearchDataPermissionHandler(protected val scopes: List<ElasticsearchDataScope>?) : ElasticsearchDataPermissionHandler {
    override fun dataScopes(): List<ElasticsearchDataScope?> {
        return if (scopes == null) emptyList<ElasticsearchDataScope>() else Collections.unmodifiableList(scopes)
    }

    override fun filterDataScopes(index: String?): List<ElasticsearchDataScope?> {
        if (scopes == null) {
            return emptyList<ElasticsearchDataScope>()
        }
        return dataScopes().stream().filter { scope: ElasticsearchDataScope? -> scope!!.includes(index) }.toList()
    }

    override fun ignorePermissionControl(index: String?): Boolean {
        return false
    }
}
