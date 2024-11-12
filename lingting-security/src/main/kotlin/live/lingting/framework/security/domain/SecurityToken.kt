package live.lingting.framework.security.domain

import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2023-04-28 12:38
 */
class SecurityToken private constructor(val type: String?, val token: String?, @JvmField val raw: String?) {
    val isAvailable: Boolean
        /**
         * token是否有效
         */
        get() = StringUtils.hasText(token)

    companion object {
        @JvmField
        val EMPTY: SecurityToken = of(null, null, null)

        @JvmStatic
        fun ofDelimiter(raw: String, delimiter: String): SecurityToken {
            if (!StringUtils.hasText(raw)) {
                return EMPTY
            }

            val split = raw.split(delimiter.toRegex(), limit = 2).toTypedArray()
            if (split.size > 1) {
                return of(split[0], split[1], raw)
            }
            return of(null, split[0], raw)
        }

        fun of(type: String?, token: String?, raw: String?): SecurityToken {
            return SecurityToken(type, token, raw)
        }
    }
}
