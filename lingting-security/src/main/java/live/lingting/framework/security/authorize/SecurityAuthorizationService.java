package live.lingting.framework.security.authorize;

import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.exception.AuthorizationException;

/**
 * 授权服务用
 *
 * @author lingting 2023-03-29 21:20
 */
public interface SecurityAuthorizationService {

	/**
	 * 校验通过则返回权限上下文信息, 未通过则返回null或者抛出 AuthorizationException
	 */
	SecurityScope validAndBuildScope(String username, String password) throws AuthorizationException;

	/**
	 * 刷新
	 */
	SecurityScope refresh(String token);

}
