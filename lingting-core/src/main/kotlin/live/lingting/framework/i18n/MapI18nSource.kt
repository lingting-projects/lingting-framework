package live.lingting.framework.i18n

import java.util.Locale

/**
 * @author lingting 2024/12/2 16:01
 */
class MapI18nSource(
    override val locale: Locale,
    val map: MutableMap<String, String>
) : I18nSource {

    override fun find(key: String): String? {
        return map[key]
    }

    fun put(key: String, value: String) {
        map[key] = value
    }

    fun remove(key: String) {
        map.remove(key)
    }

}
