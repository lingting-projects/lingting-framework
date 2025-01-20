package live.lingting.framework.security.authorize

import java.lang.reflect.Method
import java.util.function.Predicate
import live.lingting.framework.security.annotation.Authorize
import live.lingting.framework.security.domain.SecurityResultCode.A_EXPIRED
import live.lingting.framework.security.domain.SecurityResultCode.P_REJECT
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.resource.SecurityHolder
import live.lingting.framework.util.AnnotationUtils.findAnnotation
import live.lingting.framework.util.ArrayUtils.isEmpty

/**
 * @author lingting 2023-03-29 20:45
 */
open class SecurityAuthorize @JvmOverloads constructor(
    /**
     * @see live.lingting.framework.security.properties.SecurityProperties.order
     */
    @JvmField val order: Int,
    @JvmField val customizers: List<SecurityAuthorizationCustomizer> = emptyList()
) {
    companion object {
        @JvmStatic
        fun findAuthorize(cls: Class<*>?, method: Method?): Authorize? {
            if (method != null) {
                val authorize = findAnnotation(method, Authorize::class.java)
                if (authorize != null) {
                    return authorize
                }
            }
            if (cls != null) {
                return findAnnotation(cls, Authorize::class.java)
            }
            return null
        }

    }

    fun valid(cls: Class<*>?, method: Method?) {
        val authorize = findAuthorize(cls, method)
        valid(authorize)
        for (customizer in customizers) {
            customizer.valid(cls, method, authorize)
        }
    }

    /**
     * 校验当前权限数据是否满足指定注解的要求
     */
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
            valid { obj -> obj?.enabled() == true }
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
        valid { scope -> equals(scope?.roles, authorize.hasRole) }
        // 要求任一角色
        valid { scope -> contains(scope?.roles, authorize.hasAnyRole) }
        // 要求所有权限
        valid { scope -> equals(scope?.permissions, authorize.hasPermissions) }
        // 要求任一权限
        valid { scope -> contains(scope?.permissions, authorize.hasAnyPermissions) }
    }

    protected fun validNot(authorize: Authorize) {
        // @formatter:off
		// 要求未拥有所有角色
        valid{scope -> authorize.notRole.isEmpty() || !equals(scope?.roles, authorize.notRole)}
        		// 要求未拥有任一角色
        valid{scope -> authorize.notAnyRole.isEmpty() || !contains(scope?.roles, authorize.notAnyRole)}
        		// 要求未拥有所有权限
        valid{scope -> authorize.notPermissions.isEmpty() || !equals(scope?.permissions, authorize.notPermissions)}
        		// 要求未拥有任一权限
        valid{scope -> authorize.notAnyPermissions.isEmpty() || !contains(scope?.permissions, authorize.notAnyPermissions)}
    		// @formatter:on
    }

    protected fun validLogin() {
        valid { scope ->
            if (scope == null || !scope.isLogin) {
                // 未登录让前端登录
                throw A_EXPIRED.toException()
            }
            scope.enabled()
        }
    }

    protected fun contains(havaArray: Collection<String>?, needArray: Array<String>?): Boolean {
        // 需要为空. true
        if (needArray.isEmpty()) {
            return true
        }
        // 拥有为空 false
        if (havaArray.isNullOrEmpty()) {
            return false
        }

        for (need in needArray!!) {
            // 任一存在则true
            if (havaArray.contains(need)) {
                return true
            }
        }
        return false
    }

    protected fun equals(havaArray: Collection<String>?, needArray: Array<String>?): Boolean {
        // 需要为空. true
        if (needArray.isEmpty()) {
            return true
        }
        // 拥有为空 false
        if (havaArray.isNullOrEmpty()) {
            return false
        }

        for (need in needArray!!) {
            // 任一不存在则false
            if (!havaArray.contains(need)) {
                return false
            }
        }
        return true
    }

    protected fun valid(predicate: Predicate<SecurityScope?>) {
        val scope = SecurityHolder.scope()
        val flag = predicate.test(scope)
        if (!flag) {
            throw P_REJECT.toException()
        }
    }
}
