package live.lingting.framework.elasticsearch.datascope

import live.lingting.framework.datascope.HandlerType
import live.lingting.framework.elasticsearch.IndexInfo
import live.lingting.framework.elasticsearch.builder.Compare
import live.lingting.framework.elasticsearch.interceptor.Interceptor

/**
 * @author lingting 2024/11/26 11:54
 */
class DataScopeInterceptor(
    val scopes: List<ElasticsearchDataScope>
) : Interceptor {

    override fun intercept(type: HandlerType?, info: IndexInfo, compare: Compare<*, *>) {
        // 过滤数据范围
        scopes.filter {
            // 数据范围声明忽略
            if (it.ignore(type)) {
                return@filter false
            }
            true
        }.forEach {
            val query = it.handler(type, info)
            if (query != null) {
                compare.addMust(query)
            }
        }
    }

}
