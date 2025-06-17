package live.lingting.framework.mybatis.datascope

import live.lingting.framework.datascope.HandlerType
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024/11/25 14:29
 */
abstract class JSqlDataScopeParserFactory {

    val cache = ConcurrentHashMap<String, JSqlDataScopeParser>()

    fun get(type: HandlerType?, scopes: List<JSqlDataScope>): JSqlDataScopeParser {
        val key = scopes.map { it.hashCode() }.joinToString(":", type?.name ?: "-")
        val parser = cache.computeIfAbsent(key) { create(type, scopes) }
        return parser
    }

    abstract fun create(type: HandlerType?, scopes: List<JSqlDataScope>): JSqlDataScopeParser

}
