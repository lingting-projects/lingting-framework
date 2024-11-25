package live.lingting.framework.datascope.rule

import live.lingting.framework.datascope.DataScope
import live.lingting.framework.datascope.annotation.DataPermission

/**
 * 数据权限的规则抽象类
 *
 * @author hccake
 * @since 0.7.0
 */
class DataPermissionRule {

    var ignore = false

    var includeResources = arrayOf<String>()

    var excludeResources = arrayOf<String>()

    constructor()

    constructor(ignore: Boolean) {
        this.ignore = ignore
    }

    constructor(ignore: Boolean, includeResources: Array<String>, excludeResources: Array<String>) {
        this.ignore = ignore
        this.includeResources = includeResources
        this.excludeResources = excludeResources
    }

    constructor(a: DataPermission) {
        this.ignore = a.ignore
        this.includeResources = a.includeResources
        this.excludeResources = a.excludeResources
    }

    fun filter(scopes: Array<DataScope<*, *, *>>): List<DataScope<*, *, *>> {
        if (ignore) {
            return emptyList()
        }
        if (includeResources.isEmpty() && excludeResources.isEmpty()) {
            return scopes.toList()
        }
        return filter(scopes.toList())
    }

    fun filter(scopes: Collection<DataScope<*, *, *>>): List<DataScope<*, *, *>> {
        if (scopes is List) {
            return filter(scopes)
        }
        return filter(scopes.toList())
    }

    fun filter(scopes: List<DataScope<*, *, *>>): List<DataScope<*, *, *>> {
        if (ignore) {
            return emptyList()
        }
        if (includeResources.isEmpty() && excludeResources.isEmpty()) {
            return scopes
        }
        return scopes.filter {
            if (includeResources.isNotEmpty()) {
                return@filter includeResources.contains(it.resource)
            }

            if (excludeResources.isNotEmpty()) {
                return@filter !excludeResources.contains(it.resource)
            }

            true
        }
    }

}
