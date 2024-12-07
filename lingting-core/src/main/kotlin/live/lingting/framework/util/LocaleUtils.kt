package live.lingting.framework.util

import java.util.Locale

/**
 * @author lingting 2024/12/6 14:42
 */
object LocaleUtils {

    @JvmField
    val COMPATIBLE_DELIMITER = setOf("-", "_", " ")

    const val DEFAULT_DELIMITER = "-"

    @JvmStatic
    fun parseOf(str: String): Locale {
        val delimiter = COMPATIBLE_DELIMITER.firstOrNull { str.contains(it) }
        val locale = if (delimiter == null) {
            Locale.forLanguageTag(str)
        } else {
            val parts = str.split(delimiter)
            val tag = parts.joinToString(DEFAULT_DELIMITER)
            Locale.forLanguageTag(tag)
        }
        require(!locale.language.isNullOrBlank()) { "locale invalid: $str" }
        return locale
    }

    fun String.parseLocale() = parseOf(this)

}
