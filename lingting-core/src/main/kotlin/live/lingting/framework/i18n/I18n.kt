package live.lingting.framework.i18n

import live.lingting.framework.Sequence
import live.lingting.framework.api.ApiI18nProviders
import live.lingting.framework.context.Context
import live.lingting.framework.util.LocaleUtils.parseLocale
import java.util.Collections
import java.util.Locale
import java.util.ServiceConfigurationError
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024/11/30 18:26
 */
object I18n {

    @JvmStatic
    var defaultLocal: Locale = Locale.getDefault()
        set(value) {
            field = value
            // 默认值切换时, 清空已有的locale缓存
            localeMap.clear()
        }

    @JvmStatic
    val providers: List<I18nProvider> by lazy {
        ArrayList<I18nProvider>().let {
            it.add(ApiI18nProviders)

            try {
                val loaders = ServiceLoader.load(I18nProvider::class.java)

                for (provider in loaders) {
                    if (provider != null) {
                        it.add(provider)
                    }
                }

            } catch (_: ServiceConfigurationError) {
                //
            }

            Sequence.asc(it)
            Collections.unmodifiableList(it)
        }
    }

    private val sourceMap = ConcurrentHashMap<Locale, List<I18nSource>>()

    @JvmStatic
    fun sources(locale: Locale): List<I18nSource> {
        return sourceMap.computeIfAbsent(locale) {
            providers.flatMap { it.find(locale) ?: emptyList() }
        }
    }

    @JvmField
    val localeMap = ConcurrentHashMap<Locale, I18nLocal>()

    /**
     * 对应语言的替代品, 用于国际化依次查找, 这样子不用重复设置相同值
     */
    @JvmField
    val replaceMap = ConcurrentHashMap<Locale, List<Locale>>().apply {
        put(Locale.CHINESE, listOf(Locale.SIMPLIFIED_CHINESE))
        put(Locale.ENGLISH, listOf(Locale.US))
    }

    private val context = Context<Locale>({ defaultLocal })

    fun get() = context.get(defaultLocal)

    fun set(locale: Locale) = context.set(locale)

    fun remove() = context.remove()

    /**
     * 获取对应语言的所有替代品
     */
    fun replaces(locale: Locale): LinkedHashSet<Locale> = LinkedHashSet<Locale>().apply {
        // 最准确的
        add(locale)
        // 匹配的次级选项
        add(locale.language.parseLocale())
        // 替代品
        val r = replaceMap[locale]
        if (!r.isNullOrEmpty()) {
            addAll(r)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun local(locale: Locale = get()): I18nLocal {
        return localeMap.computeIfAbsent(locale) {
            val locales = replaces(locale)

            val set = LinkedHashSet<I18nSource>()

            locales.forEach {
                val v = sources(it)
                set.addAll(v)
            }

            I18nLocal(locale, set.toList())
        }
    }

    @JvmStatic
    @JvmOverloads
    fun find(key: String, locale: Locale = get()): String? {
        val local = local(locale)
        val find = local.find(key)
        if (find == null && locale != defaultLocal) {
            return find(key, defaultLocal)
        }
        return find
    }

    @JvmStatic
    @JvmOverloads
    fun find(key: String, defaultValue: String, locale: Locale = get()): String {
        val local = local(locale)
        return local.find(key, defaultValue)
    }

}
