package live.lingting.framework.datascope.holder

import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.datascope.JsqlDataScope

/**
 * 该类用于存储，不需数据权限处理的 mappedStatementId 集合
 *
 * @author hccake
 */
class MappedStatementIdsWithoutDataScope private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * key: DataScope class，value: 该 DataScope 不需要处理的 mappedStatementId 集合
         */
        private val WITHOUT_MAPPED_STATEMENT_ID_MAP: MutableMap<Class<out JsqlDataScope>, HashSet<String>> = ConcurrentHashMap()

        /**
         * 给所有的 DataScope 对应的忽略列表添加对应的 mappedStatementId
         *
         * @param dataScopeList     数据范围集合
         * @param mappedStatementId mappedStatementId
         */
        fun addToWithoutSet(dataScopeList: List<JsqlDataScope>, mappedStatementId: String) {
            for (dataScope in dataScopeList) {
                val dataScopeClass: Class<out JsqlDataScope> = dataScope.javaClass
                val set = WITHOUT_MAPPED_STATEMENT_ID_MAP.computeIfAbsent(
                    dataScopeClass
                ) { key: Class<out JsqlDataScope> -> HashSet() }
                set.add(mappedStatementId)
            }
        }

        /**
         * 是否可以忽略权限控制，检查当前 mappedStatementId 是否存在于所有需要控制的 dataScope 对应的忽略列表中
         *
         * @param dataScopeList     数据范围集合
         * @param mappedStatementId mappedStatementId
         * @return 忽略控制返回 true
         */
        fun onAllWithoutSet(dataScopeList: List<JsqlDataScope>, mappedStatementId: String): Boolean {
            for (dataScope in dataScopeList) {
                val dataScopeClass: Class<out JsqlDataScope> = dataScope.javaClass
                val set = WITHOUT_MAPPED_STATEMENT_ID_MAP.computeIfAbsent(
                    dataScopeClass
                ) { key: Class<out JsqlDataScope> -> HashSet() }
                if (!set.contains(mappedStatementId)) {
                    return false
                }
            }
            return true
        }
    }
}
