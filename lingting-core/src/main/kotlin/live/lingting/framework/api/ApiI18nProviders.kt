package live.lingting.framework.api

import live.lingting.framework.i18n.I18nProvider
import live.lingting.framework.i18n.I18nSource
import live.lingting.framework.i18n.MapI18nSource
import live.lingting.framework.resource.Resource
import live.lingting.framework.util.LocaleUtils.parseLocale
import live.lingting.framework.util.ResourceUtils
import live.lingting.framework.util.StreamUtils
import java.util.Locale

/**
 *
 * @author lingting 2024/12/2 15:25
 */
object ApiI18nProviders : I18nProvider {

    val suffix = ".properties"

    val prefix = "api-"

    val map = HashMap<Locale, MapI18nSource>()

    init {
        ResourceUtils.scan("i18n") {
            it.isFile && it.name.endsWith(suffix) && it.name.startsWith(prefix)
        }.forEach { loadByResource(it) }
    }

    @Synchronized
    fun put(locale: Locale, source: Map<String, String>) {
        // 合并同一语言数据
        map.compute(locale) { _, v ->
            v?.putAll(source) ?: MapI18nSource(locale, source)
        }
    }

    fun loadByResource(resource: Resource) {
        val tag = resource.name.substring(prefix.length, resource.name.length - suffix.length)
        val locale = tag.parseLocale()
        val lines = resource.stream().use { StreamUtils.toString(it) }.lines()
        val kv = HashMap<String, String>()
        for (line in lines) {
            val index = line.indexOf('=')
            if (index == -1) {
                continue
            }
            val key = line.substring(0, index)
            val value = line.substring(index + 1)
            kv[key] = value
        }
        put(locale, kv)
    }

    override fun find(locale: Locale): Collection<I18nSource>? {
        val source = map[locale]
        return if (source == null) null else listOf(source)
    }

}
