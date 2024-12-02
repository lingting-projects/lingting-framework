package live.lingting.framework.api

import java.util.Locale
import live.lingting.framework.i18n.I18nProvider
import live.lingting.framework.i18n.I18nSource
import live.lingting.framework.i18n.MapI18nSource
import live.lingting.framework.util.ResourceUtils
import live.lingting.framework.util.StreamUtils

/**
 *
 * @author lingting 2024/12/2 15:25
 */
class ApiI18nProviders : I18nProvider {
    val suffix = ".properties"
    val prefix = "api-"

    override fun load(): Collection<I18nSource> {
        val list = ArrayList<I18nSource>()
        val resources = ResourceUtils.scan("i18n") {
            it.isFile && it.name.endsWith(suffix) && it.name.startsWith(prefix)
        }
        for (resource in resources) {
            val tag = resource.name.substring(prefix.length, resource.name.length - suffix.length)
            val locale = Locale.forLanguageTag(tag)
            val lines = resource.stream().use { StreamUtils.toString(it) }.lines()
            val map = HashMap<String, String>()
            for (line in lines) {
                val index = line.indexOf('=')
                if (index == -1) {
                    continue
                }
                val key = line.substring(0, index)
                val value = line.substring(index + 1)
                map[key] = value
            }
            list.add(MapI18nSource(locale, map))
        }
        return list
    }

}
