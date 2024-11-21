package live.lingting.framework.elasticsearch.datascope

import co.elastic.clients.elasticsearch._types.query_dsl.Query

/**
 * @author lingting 2023-06-27 10:58
 */
interface ElasticsearchDataScope {
    /**
     * 数据所对应的资源
     * @return 资源标识
     */
    val resource: String

    /**
     * 判断当前数据权限范围是否需要管理此索引
     * @param index 当前需要处理的索引名
     * @return 如果当前数据权限范围包含当前索引名，则返回 true。，否则返回 false
     */
    fun includes(index: String): Boolean

    fun invoke(index: String): Query
}
