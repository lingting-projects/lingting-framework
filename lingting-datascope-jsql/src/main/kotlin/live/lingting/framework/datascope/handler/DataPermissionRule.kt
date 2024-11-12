package live.lingting.framework.datascope.handler

import live.lingting.framework.datascope.annotation.DataPermission

/**
 * 数据权限的规则抽象类
 *
 * @author hccake
 * @since 0.7.0
 */
class DataPermissionRule {
    private var ignore = false

    private var includeResources = arrayOfNulls<String>(0)

    private var excludeResources = arrayOfNulls<String>(0)

    constructor()

    constructor(ignore: Boolean) {
        this.ignore = ignore
    }

    constructor(ignore: Boolean, includeResources: Array<String?>, excludeResources: Array<String?>) {
        this.ignore = ignore
        this.includeResources = includeResources
        this.excludeResources = excludeResources
    }

    constructor(dataPermission: DataPermission) {
        this.ignore = dataPermission.ignore
        this.includeResources = dataPermission.includeResources
        this.excludeResources = dataPermission.excludeResources
    }

    /**
     * 当前类或方法是否忽略数据权限
     * @return boolean 默认返回 false
     */
    fun ignore(): Boolean {
        return ignore
    }

    /**
     * 仅对指定资源类型进行数据权限控制，只在开启情况下有效，当该数组有值时，exclude不生效
     * @see DataPermission.excludeResources
     *
     * @return 资源类型数组
     */
    fun includeResources(): Array<String?> {
        return includeResources
    }

    /**
     * 对指定资源类型跳过数据权限控制，只在开启情况下有效，当该includeResources有值时，exclude不生效
     * @see DataPermission.includeResources
     *
     * @return 资源类型数组
     */
    fun excludeResources(): Array<String?> {
        return excludeResources
    }

    fun setIgnore(ignore: Boolean): DataPermissionRule {
        this.ignore = ignore
        return this
    }

    fun setIncludeResources(includeResources: Array<String?>): DataPermissionRule {
        this.includeResources = includeResources
        return this
    }

    fun setExcludeResources(excludeResources: Array<String?>): DataPermissionRule {
        this.excludeResources = excludeResources
        return this
    }
}
