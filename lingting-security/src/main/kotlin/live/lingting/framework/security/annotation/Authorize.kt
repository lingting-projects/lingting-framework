package live.lingting.framework.security.annotation

import java.lang.annotation.Inherited

/**
 * 鉴权, 默认为登录且用户可用 即可访问. 串行, 任一属性要求不通过即不允许
 *
 * @author lingting 2023-03-29 20:38
 */
@Inherited
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS)
annotation class Authorize(
    /**
     * 是否允许匿名, 为true时, 登录和未登录均允许访问. 优先级最高
     */
    val anyone: Boolean = false,
    /**
     * 是否仅已启用用户访问
     */
    val onlyEnabled: Boolean = true,
    /**
     * 必须拥有所有指定角色才可以访问, 为空时允许所有角色访问.
     */
    val hasRole: Array<String> = [],
    /**
     * 必须拥有任一指定角色才可以访问, 为空时允许所有角色访问.
     */
    val hasAnyRole: Array<String> = [],
    /**
     * 必须拥有所有指定权限才可以访问, 为空时允许所有权限访问.
     */
    val hasPermissions: Array<String> = [],
    /**
     * 必须拥有任一指定权限才可以访问, 为空时允许所有权限访问.
     */
    val hasAnyPermissions: Array<String> = [],
    /**
     * 必须未拥有所有指定角色才可以访问, 为空时允许所有角色访问.
     */
    val notRole: Array<String> = [],
    /**
     * 必须未拥有任一指定角色才可以访问, 为空时允许所有角色访问.
     */
    val notAnyRole: Array<String> = [],
    /**
     * 必须未拥有所有指定权限才可以访问, 为空时允许所有权限访问.
     */
    val notPermissions: Array<String> = [],
    /**
     * 必须未拥有任一指定权限才可以访问, 为空时允许所有权限访问.
     */
    val notAnyPermissions: Array<String> = []
)
