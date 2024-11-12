package live.lingting.framework.security.authorize

import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.exception.AuthorizationException

/**
 * 授权服务用
 *
 * @author lingting 2023-03-29 21:20
 */
interface SecurityAuthorizationService {
    /**
     * 校验通过则返回权限上下文信息, 未通过则返回null或者抛出 AuthorizationException
     */
    @Throws(AuthorizationException::class)
    fun validAndBuildScope(username: String?, password: String?): SecurityScope?

    /**
     * 刷新
     */
    fun refresh(token: String?): SecurityScope?
}
