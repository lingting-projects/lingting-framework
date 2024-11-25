package live.lingting.framework.datascope

import live.lingting.framework.datascope.rule.DataPermissionRuleHolder

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
     * 判断当前上下文是否忽略此数据权限
     */
    fun ignore(): Boolean {
        val rule = DataPermissionRuleHolder.peek()
        return rule != null && rule.ignore
    }

    /**
     * 判断当前数据权限范围是否需要管理此数据
     * @param p 当前处理的数据相关参数
     * @return 如果当前数据权限范围包含当前处理的数据，则返回 true。，否则返回 false
     */
    fun includes(p: String): Boolean

    fun handler(p: D): R?

}
