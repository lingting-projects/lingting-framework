package live.lingting.framework.security.authorize

import live.lingting.framework.security.annotation.Authorize
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.exception.PermissionsException
import live.lingting.framework.security.resource.SecurityHolder
import live.lingting.framework.util.AnnotationUtils
import live.lingting.framework.util.ArrayUtils
import live.lingting.framework.util.CollectionUtils
import java.lang.reflect.Method
import java.util.function.Predicate

/**
 * @author lingting 2023-03-29 20:45
 */
class SecurityAuthorize(@JvmField val order: Int) {
    fun findAuthorize(cls: Class<*>?, method: Method?): Authorize? {
        if (method != null) {
            val authorize = AnnotationUtils.findAnnotation(method, Authorize::class.java)
            if (authorize != null) {
                return authorize
            }
        }
        return AnnotationUtils.findAnnotation(cls!!, Authorize::class.java)
    }

    @Throws(AuthorizationException::class)
    fun valid(cls: Class<*>?, method: Method?) {
        val authorize = findAuthorize(cls, method)
        valid(authorize)
    }

    /**
     * 校验当前权限数据是否满足指定注解的要求
     */
    @Throws(PermissionsException::class)
    fun valid(authorize: Authorize?) {
        val allowAnyone = authorize != null && authorize.anyone
        val allowDisabled = authorize != null && !authorize.onlyEnabled

        // 允许匿名, 直接执行
        if (allowAnyone) {
            return
        }

        // 非匿名, 要求登录
        validLogin()

        // 不允许未启用用户访问, 校验是否启用
        if (!allowDisabled) {
            valid { obj: SecurityScope? -> obj!!.enabled() }
        }

        // 进行配置校验
        if (authorize != null) {
            // 校验拥有配置
            validHas(authorize)
            // 校验未拥有配置
            validNot(authorize)
        }
    }

    protected fun validHas(authorize: Authorize) {
        // 要求所有角色
        valid { scope: SecurityScope? -> equals(scope.getRoles(), authorize.hasRole) }
        // 要求任一角色
        valid { scope: SecurityScope? -> contains(scope.getRoles(), authorize.hasAnyRole) }
        // 要求所有权限
        valid { scope: SecurityScope? -> equals(scope.getPermissions(), authorize.hasPermissions) }
        // 要求任一权限
        valid { scope: SecurityScope? -> contains(scope.getPermissions(), authorize.hasAnyPermissions) }
    }

    protected fun validNot(authorize: Authorize) {
        // @formatter:off
		// 要求未拥有所有角色
        valid{scope: SecurityScope? -> ArrayUtils.isEmpty(authorize.notRole) || !equals(scope.getRoles(), authorize.notRole)}
        		// 要求未拥有任一角色
        valid{scope: SecurityScope? -> ArrayUtils.isEmpty(authorize.notAnyRole) || !contains(scope.getRoles(), authorize.notAnyRole)}
        		// 要求未拥有所有权限
        valid{scope: SecurityScope? -> ArrayUtils.isEmpty(authorize.notPermissions) || !equals(scope.getPermissions(), authorize.notPermissions)}
        		// 要求未拥有任一权限
        valid{scope: SecurityScope? -> ArrayUtils.isEmpty(authorize.notAnyPermissions) || !contains(scope.getPermissions(), authorize.notAnyPermissions)}
    		// @formatter:on
    }

    protected fun validLogin() {
        valid { scope: SecurityScope? ->
            if (scope == null || !scope.isLogin) {
                // 未登录让前端登录
                throw AuthorizationException()
            }
            scope.enabled()
        }
    }

    protected fun contains(havaArray: Collection<String?>, needArray: Array<String>): Boolean {
        // 需要为空. true
        if (ArrayUtils.isEmpty(needArray)) {
            return true
        }
        // 拥有为空 false
        if (CollectionUtils.isEmpty(havaArray)) {
            return false
        }

        for (need in needArray) {
            // 任一存在则true
            if (havaArray.contains(need)) {
                return true
            }
        }
        return false
    }

    protected fun equals(havaArray: Collection<String?>, needArray: Array<String>): Boolean {
        // 需要为空. true
        if (ArrayUtils.isEmpty(needArray)) {
            return true
        }
        // 拥有为空 false
        if (CollectionUtils.isEmpty(havaArray)) {
            return false
        }

        for (need in needArray) {
            // 任一不存在则false
            if (!havaArray.contains(need)) {
                return false
            }
        }
        return true
    }

    protected fun valid(predicate: Predicate<SecurityScope?>) {
        val scope: SecurityScope = SecurityHolder.Companion.scope()
        val flag = predicate.test(scope)
        if (!flag) {
            throw PermissionsException()
        }
    }
}