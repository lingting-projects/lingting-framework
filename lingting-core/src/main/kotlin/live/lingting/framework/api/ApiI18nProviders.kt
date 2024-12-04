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

    val map: Map<Locale, I18nSource> by lazy {
        HashMap<Locale, MapI18nSource>().apply {
            val resources = ResourceUtils.scan("i18n") {
                it.isFile && it.name.endsWith(suffix) && it.name.startsWith(prefix)
            }
            for (resource in resources) {
                val tag = resource.name.substring(prefix.length, resource.name.length - suffix.length)
                val locale = Locale.forLanguageTag(tag)
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
                // 合并同一语言数据
                compute(locale) { _, v ->
                    v?.putAll(kv) ?: MapI18nSource(locale, kv)
                }
            }
        }

    }

    override fun find(locale: Locale): Collection<I18nSource>? {
        val source = map[locale]
        return if (source == null) null else listOf(source)
    }

}
