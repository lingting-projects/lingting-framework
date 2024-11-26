package live.lingting.framework.elasticsearch.polymerize

import live.lingting.framework.elasticsearch.IndexInfo

/**
 * @author lingting 2024/11/26 13:46
 */
class NonPolymerize : Polymerize {
    override fun <T> index(info: IndexInfo, o: T): String {
        return info.index
    }
}
