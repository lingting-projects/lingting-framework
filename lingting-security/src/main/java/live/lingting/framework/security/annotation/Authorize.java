package live.lingting.framework.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鉴权, 默认为登录且用户可用 即可访问. 串行, 任一属性要求不通过即不允许
 *
 * @author lingting 2023-03-29 20:38
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Authorize {

	/**
	 * 是否允许匿名, 为true时, 登录和未登录均允许访问. 优先级最高
	 */
	boolean anyone() default false;

	/**
	 * 是否仅已启用用户访问
	 */
	boolean onlyEnabled() default true;

	/**
	 * 必须拥有所有指定角色才可以访问, 为空时允许所有角色访问.
	 */
	String[] hasRole() default {};

	/**
	 * 必须拥有任一指定角色才可以访问, 为空时允许所有角色访问.
	 */
	String[] hasAnyRole() default {};

	/**
	 * 必须拥有所有指定权限才可以访问, 为空时允许所有权限访问.
	 */
	String[] hasPermissions() default {};

	/**
	 * 必须拥有任一指定权限才可以访问, 为空时允许所有权限访问.
	 */
	String[] hasAnyPermissions() default {};

	/**
	 * 必须未拥有所有指定角色才可以访问, 为空时允许所有角色访问.
	 */
	String[] notRole() default {};

	/**
	 * 必须未拥有任一指定角色才可以访问, 为空时允许所有角色访问.
	 */
	String[] notAnyRole() default {};

	/**
	 * 必须未拥有所有指定权限才可以访问, 为空时允许所有权限访问.
	 */
	String[] notPermissions() default {};

	/**
	 * 必须未拥有任一指定权限才可以访问, 为空时允许所有权限访问.
	 */
	String[] notAnyPermissions() default {};

}
