package live.lingting.framework.security.authorize;

import live.lingting.framework.security.annotation.Authorize;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.exception.AuthorizationException;
import live.lingting.framework.security.exception.PermissionsException;
import live.lingting.framework.security.resource.SecurityHolder;
import live.lingting.framework.util.AnnotationUtils;
import live.lingting.framework.util.ArrayUtils;
import live.lingting.framework.util.CollectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author lingting 2023-03-29 20:45
 */
@Getter
@RequiredArgsConstructor
public class SecurityAuthorize {

	private final int order;

	public Authorize findAuthorize(Class<?> cls, Method method) {
		if (method != null) {
			Authorize authorize = AnnotationUtils.findAnnotation(method, Authorize.class);
			if (authorize != null) {
				return authorize;
			}
		}
		return AnnotationUtils.findAnnotation(cls, Authorize.class);
	}

	public void valid(Class<?> cls, Method method) throws AuthorizationException {
		Authorize authorize = findAuthorize(cls, method);
		valid(authorize);
	}

	/**
	 * 校验当前权限数据是否满足指定注解的要求
	 */
	public void valid(Authorize authorize) throws PermissionsException {
		boolean allowAnyone = authorize != null && authorize.anyone();
		boolean allowDisabled = authorize != null && !authorize.onlyEnabled();

		// 允许匿名, 直接执行
		if (allowAnyone) {
			return;
		}

		// 非匿名, 要求登录
		validLogin();

		// 不允许未启用用户访问, 校验是否启用
		if (!allowDisabled) {
			valid(SecurityScope::enabled);
		}

		// 进行配置校验
		if (authorize != null) {
			// 校验拥有配置
			validHas(authorize);
			// 校验未拥有配置
			validNot(authorize);
		}
	}

	protected void validHas(Authorize authorize) {
		// 要求所有角色
		valid(scope -> equals(scope.getRoles(), authorize.hasRole()));
		// 要求任一角色
		valid(scope -> contains(scope.getRoles(), authorize.hasAnyRole()));
		// 要求所有权限
		valid(scope -> equals(scope.getPermissions(), authorize.hasPermissions()));
		// 要求任一权限
		valid(scope -> contains(scope.getPermissions(), authorize.hasAnyPermissions()));
	}

	protected void validNot(Authorize authorize) {
		// @formatter:off
		// 要求未拥有所有角色
		valid(scope ->  ArrayUtils.isEmpty(authorize.notRole()) || !equals(scope.getRoles(), authorize.notRole()));
		// 要求未拥有任一角色
		valid(scope ->  ArrayUtils.isEmpty(authorize.notAnyRole()) || !contains(scope.getRoles(), authorize.notAnyRole()));
		// 要求未拥有所有权限
		valid(scope ->  ArrayUtils.isEmpty(authorize.notPermissions()) || !equals(scope.getPermissions(), authorize.notPermissions()));
		// 要求未拥有任一权限
		valid(scope ->  ArrayUtils.isEmpty(authorize.notAnyPermissions()) || !contains(scope.getPermissions(), authorize.notAnyPermissions()));
		// @formatter:on
	}

	protected void validLogin() {
		valid(scope -> {
			if (scope == null || !scope.isLogin()) {
				// 未登录让前端登录
				throw new AuthorizationException();
			}
			// 状态异常
			return scope.enabled();
		});
	}

	protected boolean contains(Collection<String> havaArray, String[] needArray) {
		// 需要为空. true
		if (ArrayUtils.isEmpty(needArray)) {
			return true;
		}
		// 拥有为空 false
		if (CollectionUtils.isEmpty(havaArray)) {
			return false;
		}

		for (String need : needArray) {
			// 任一存在则true
			if (havaArray.contains(need)) {
				return true;
			}
		}
		return false;
	}

	protected boolean equals(Collection<String> havaArray, String[] needArray) {
		// 需要为空. true
		if (ArrayUtils.isEmpty(needArray)) {
			return true;
		}
		// 拥有为空 false
		if (CollectionUtils.isEmpty(havaArray)) {
			return false;
		}

		for (String need : needArray) {
			// 任一不存在则false
			if (!havaArray.contains(need)) {
				return false;
			}
		}
		return true;
	}

	protected void valid(Predicate<SecurityScope> predicate) {
		SecurityScope scope = SecurityHolder.scope();
		boolean flag = predicate.test(scope);
		if (!flag) {
			throw new PermissionsException();
		}
	}

}
