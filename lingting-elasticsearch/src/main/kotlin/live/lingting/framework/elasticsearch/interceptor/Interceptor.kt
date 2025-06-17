package live.lingting.framework.elasticsearch.interceptor

import live.lingting.framework.Sequence
import live.lingting.framework.datascope.HandlerType
import live.lingting.framework.elasticsearch.IndexInfo
import live.lingting.framework.elasticsearch.builder.Compare

/**
 * @author lingting 2024/11/26 11:45
 */
interface Interceptor : Sequence {

    fun intercept(type: HandlerType?, info: IndexInfo, compare: Compare<*, *>)

    override val sequence: Int
        get() = 0

}
