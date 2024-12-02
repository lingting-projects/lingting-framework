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

    override fun load(): Collection<I18nSource> {
        val map = mutableMapOf(key to value)
        val element = MapI18nSource(locale, map)
        return listOf(element)
    }
}
