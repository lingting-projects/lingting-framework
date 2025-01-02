package live.lingting.framework.security.authorize

import java.lang.reflect.Method
import live.lingting.framework.security.annotation.Authorize
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.exception.PermissionsException

/**
 * @author lingting 2025/1/2 16:10
 */
@Suppress("kotlin:S6517")
interface SecurityAuthorizationCustomizer {

    @Throws(PermissionsException::class, AuthorizationException::class)
    fun valid(cls: Class<*>?, method: Method?, authorize: Authorize?)

}
