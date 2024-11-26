package live.lingting.framework.elasticsearch.datascope

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import live.lingting.framework.datascope.DataScope
import live.lingting.framework.elasticsearch.IndexInfo

/**
 * @author lingting 2023-06-27 10:58
 */
interface ElasticsearchDataScope : DataScope<String, IndexInfo, Query> {

}
