package live.lingting.framework.elasticsearch.interceptor

import live.lingting.framework.Sequence
import live.lingting.framework.elasticsearch.IndexInfo
import live.lingting.framework.elasticsearch.builder.QueryBuilder

/**
 * @author lingting 2024/11/26 11:45
 */
interface Interceptor : Sequence {

    fun intercept(info: IndexInfo, builder: QueryBuilder<*>)

    override val sequence: Int
        get() = 0

}
