package live.lingting.framework.datascope.handler

import live.lingting.framework.datascope.JsqlDataScope
import live.lingting.framework.datascope.holder.DataPermissionRuleHolder
import live.lingting.framework.datascope.holder.MappedStatementIdsWithoutDataScope


/**
 * 默认的数据权限控制处理器
 *
 * @author Hccake 2021/1/27
 * @version 1.0
 */
class DefaultDataPermissionHandler(private val dataScopes: List<JsqlDataScope>?) : DataPermissionHandler {
    /**
     * 系统配置的所有的数据范围
     *
     * @return 数据范围集合
     */
    override fun dataScopes(): List<JsqlDataScope>? {
        return dataScopes
    }

    /**
     * 系统配置的所有的数据范围
     *
     * @param mappedStatementId Mapper方法ID
     * @return 数据范围集合
     */
    override fun filterDataScopes(mappedStatementId: String?): List<JsqlDataScope>? {
        if (this.dataScopes == null || dataScopes.isEmpty()) {
            return ArrayList()
        }
        // 获取权限规则
        val dataPermissionRule: DataPermissionRule = DataPermissionRuleHolder.peek()
        return filterDataScopes(dataPermissionRule)
    }

    /**
     *
     *
     * 是否忽略权限控制
     *
     * 若当前的 mappedStatementId 存在于 <Code>MappedStatementIdsWithoutDataScope<Code></Code>
     * 中，则表示无需处理
     *
     * @param dataScopeList     当前需要控制的 dataScope 集合
     * @param mappedStatementId Mapper方法ID
     * @return always false
    </Code> */
    override fun ignorePermissionControl(dataScopeList: List<JsqlDataScope>, mappedStatementId: String): Boolean {
        return MappedStatementIdsWithoutDataScope.onAllWithoutSet(dataScopeList, mappedStatementId)
    }

    /**
     * 根据数据权限规则过滤出 dataScope 列表
     *
     * @param dataPermissionRule 数据权限规则
     * @return List<DataScope>
    </DataScope> */
    protected fun filterDataScopes(dataPermissionRule: DataPermissionRule?): List<JsqlDataScope>? {
        if (dataPermissionRule == null) {
            return dataScopes
        }

        if (dataPermissionRule.ignore()) {
            return ArrayList()
        }

        // 当指定了只包含的资源时，只对该资源的DataScope
        if (dataPermissionRule.includeResources().size > 0) {
            val a: Set<String?> = HashSet(Arrays.asList(*dataPermissionRule.includeResources()))
            return dataScopes!!.stream().filter { x: JsqlDataScope -> a.contains(x.resource) }.toList()
        }

        // 当未指定只包含的资源，且指定了排除的资源时，则排除此部分资源的 DataScope
        if (dataPermissionRule.excludeResources().size > 0) {
            val a: Set<String?> = HashSet(Arrays.asList(*dataPermissionRule.excludeResources()))
            return dataScopes!!.stream().filter { x: JsqlDataScope -> !a.contains(x.resource) }.toList()
        }

        return dataScopes
    }
}
