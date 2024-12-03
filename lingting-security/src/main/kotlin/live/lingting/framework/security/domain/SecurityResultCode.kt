package live.lingting.framework.security.domain

import live.lingting.framework.api.ResultCode
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.exception.PermissionsException

/**202202
 * @author lingting 2024/12/3 20:17
 */
enum class SecurityResultCode(
    override val code: Long,
    override val message: String,
    private val throwAuthorize: Boolean
) : ResultCode {

    A_LOGIN_ILLEGAL(2022020000, "Login is illegal!", true),

    A_PASSWORD_ILLEGAL(2022020001, "Password is illegal!", true),

    A_EXPIRED(2022020002, "Authorization is expired!", true),

    A_TOKEN_INVALID(2022020003, "Token is invalid!", true),

    P_REJECT(2022020004, "Access is rejected!", false),

    ;

    override fun toException(e: Exception?): Exception {
        val i18n = i18nMessage()
        if (throwAuthorize) {
            return AuthorizationException(i18n, e)
        }
        return PermissionsException(i18n, e)
    }
}
