package live.lingting.framework.mybatis.datascope

import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024/11/25 14:29
 */
abstract class JSqlDataScopeParserFactory {

    val cache = ConcurrentHashMap<List<JSqlDataScope>, JSqlDataScopeParser>()

    fun get(scopes: List<JSqlDataScope>): JSqlDataScopeParser {
        val filter = cache.entries.filter { it.key == scopes || (it.key.size == scopes.size && it.key.containsAll(scopes)) }
        val find = filter.firstOrNull()
        val parser = if (find == null) {
            val p = create(scopes)
            cache[scopes] = p
            p
        } else {
            if (filter.size > 1) {
                filter.forEach {
                    if (it.key != find.key) {
                        cache.remove(it.key, it.value)
                    }
                }
            }
            find.value
        }
        return parser
    }

    abstract fun create(scopes: List<JSqlDataScope>): JSqlDataScopeParser

}
