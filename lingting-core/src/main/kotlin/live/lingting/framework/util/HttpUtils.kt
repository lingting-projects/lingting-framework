package live.lingting.framework.util

import java.util.regex.Pattern

/**
 * @author lingting 2022/10/28 17:54
 */
object HttpUtils {

    const val PATTERN_REGEX: String = "^https?://(([a-zA-Z0-9.\\-]+)(:[0-9]+)?)(/.*)?\$"

    @JvmField
    val PATTERN: Pattern = Pattern.compile(PATTERN_REGEX)!!

    @JvmStatic
    fun isHttpUrl(string: String?): Boolean {
        if (string.isNullOrBlank()) {
            return false
        }
        val matcher = PATTERN.matcher(string)
        return matcher.matches()
    }

    @JvmStatic
    fun pickHost(string: String?): String? {
        if (string.isNullOrBlank()) {
            return null
        }
        val matcher = PATTERN.matcher(string)
        if (matcher.matches()) {
            return matcher.group(1)
        }
        return null
    }

}

