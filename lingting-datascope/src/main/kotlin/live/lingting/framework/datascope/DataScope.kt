package live.lingting.framework.datascope

import live.lingting.framework.datascope.rule.DataScopeRuleHolder

/**
 * @param I 当前处理的数据相关参数
 * @param D 当前处理数据信息
 * @param R 处理结果返回值
 * @author lingting
 */
interface DataScope<I, D, R> {
    /**
     * 数据所对应的资源
     * @return 资源标识
     */
    val resource: String

    /**
     * 判断当前上下文是否忽略此数据范围
     */
    fun ignore(): Boolean {
        val rule = DataScopeRuleHolder.peek()
        // 未设置不忽略
        if (rule == null) {
            return false
        }
        // 已设置忽略时直接忽略
        if (rule.ignore) {
            return true
        }
        val include = rule.includeResources
        if (include.isNotEmpty()) {
            // 已设置包含资源且包含当前数据范围, 不忽略
            return !include.contains(resource)
        }
        val exclude = rule.excludeResources
        // 已设置排除资源且包含当前数据范围, 忽略
        return exclude.isNotEmpty() && exclude.contains(resource)
    }

    /**
     * 判断当前数据范围范围是否需要管理此数据
     * @param p 当前处理的数据相关参数
     * @return 如果当前数据范围范围包含当前处理的数据，则返回 true。，否则返回 false
     */
    fun includes(p: String): Boolean

    fun handler(p: D): R?

}
