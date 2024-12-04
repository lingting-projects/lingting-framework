package live.lingting.framework.i18n

import java.util.Locale

/**
 * @author lingting 2024/12/2 16:05
 */
class TestSpiI18nProvider : I18nProvider {

    companion object {

        val locale: Locale = Locale.CANADA

        val key = locale.toString()

        val value = "你好"

    }

    override fun find(locale: Locale): Collection<I18nSource>? {
        if (locale != TestSpiI18nProvider.locale) {
            return null
        }

        val map = mutableMapOf(key to value)
        val element = MapI18nSource(Companion.locale, map)
        return listOf(element)
    }

}
