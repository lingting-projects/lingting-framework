package live.lingting.framework.elasticsearch.datascope

import live.lingting.framework.elasticsearch.IndexInfo
import live.lingting.framework.elasticsearch.builder.QueryBuilder
import live.lingting.framework.elasticsearch.interceptor.Interceptor

/**
 * @author lingting 2024/11/26 11:54
 */
class DataPermissionInterceptor(
    val scopes: List<ElasticsearchDataScope>
) : Interceptor {

    override fun intercept(info: IndexInfo, builder: QueryBuilder<*>) {       // 过滤数据权限
        scopes.filter {
            // 数据权限声明忽略
            if (it.ignore()) {
                return@filter false
            }
            true
        }.forEach {
            val query = it.handler(info)
            if (query != null) {
                builder.addMust(query)
            }
        }
    }

}
