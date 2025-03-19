package live.lingting.framework.datascope

import live.lingting.framework.datascope.HandlerType.DELETE
import live.lingting.framework.datascope.HandlerType.QUERY
import live.lingting.framework.datascope.HandlerType.UPDATE
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

    private fun ignore(flag: Boolean, include: Array<String>, exclude: Array<String>): Boolean {
        // 已设置忽略时直接忽略
        if (flag) {
            return true
        }

        if (include.isNotEmpty() && include.contains(resource)) {
            // 已设置包含资源且包含当前数据范围, 不忽略
            return false
        }

        if (exclude.isNotEmpty() && exclude.contains(resource)) {
            // 已设置排除资源且包含当前数据范围, 忽略
            return true
        }
        return false
    }

    /**
     * 判断当前上下文是否忽略此数据范围
     */
    fun ignore(type: HandlerType?): Boolean {
        val rule = DataScopeRuleHolder.peek()
        // 未设置不忽略
        if (rule == null) {
            return false
        }

        if (ignore(rule.ignore, rule.includeResources, rule.excludeResources)) {
            return true
        }

        return when (type) {
            QUERY -> {
                ignore(rule.ignoreQuery, rule.includeQueryResources, rule.excludeQueryResources)
            }

            UPDATE -> {
                ignore(rule.ignoreUpdate, rule.includeUpdateResources, rule.excludeUpdateResources)
            }

            DELETE -> {
                ignore(rule.ignoreDelete, rule.includeDeleteResources, rule.excludeDeleteResources)
            }

            else -> false
        }
    }

    /**
     * 判断当前数据范围范围是否需要管理此数据
     * @param p 当前处理的数据相关参数
     * @return 如果当前数据范围范围包含当前处理的数据，则返回 true。，否则返回 false
     */
    fun includes(type: HandlerType?, p: String): Boolean

    fun handler(type: HandlerType?, p: D): R?

}
