package live.lingting.framework.elasticsearch.datascope

/**
 * @author lingting 2024-07-01 17:29
 */
class AllowElasticsearchDataPermissionHandler : ElasticsearchDataPermissionHandler {
    override fun dataScopes(): List<ElasticsearchDataScope> {
        return listOf<ElasticsearchDataScope>()
    }

    override fun filterDataScopes(index: String): List<ElasticsearchDataScope> {
        return listOf<ElasticsearchDataScope>()
    }

    override fun ignorePermissionControl(index: String): Boolean {
        return true
    }
}
