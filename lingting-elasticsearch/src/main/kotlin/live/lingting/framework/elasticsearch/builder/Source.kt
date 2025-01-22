package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch.core.search.SourceConfig
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.util.ElasticsearchUtils

/**
 * @author lingting 2025/1/21 17:09
 */
interface Source<E, B : Source<E, B>> {

    fun source(source: SourceConfig?): B

    fun include(vararg fields: String): B {
        return include(fields.toList())
    }

    fun include(fields: Collection<String>): B {
        val of = SourceConfig.of { sc -> sc.filter { sf -> sf.includes(fields.toList()) } }
        return source(of)
    }

    fun include(vararg array: EFunction<E, *>): B {
        return includeLambda(array.toList())
    }

    fun includeLambda(collection: Collection<EFunction<E, *>>): B {
        return include(collection.map { ElasticsearchUtils.fieldName(it) })
    }

    fun buildSource(): SourceConfig?

}
