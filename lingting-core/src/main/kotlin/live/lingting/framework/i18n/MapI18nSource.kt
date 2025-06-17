package live.lingting.framework.i18n

import java.util.Locale

/**
 * @author lingting 2024/12/2 16:01
 */
class MapI18nSource(
    override val locale: Locale,
    map: Map<String, String>,
) : I18nSource {
    val map: MutableMap<String, String> = map.toMutableMap()

    override fun find(key: String): String? {
        return map[key]
    }

    fun put(key: String, value: String): MapI18nSource {
        map[key] = value
        return this
    }

    fun putAll(map: Map<String, String>): MapI18nSource {
        this.map.putAll(map)
        return this
    }

    fun remove(key: String): String? {
        return map.remove(key)
    }

}
