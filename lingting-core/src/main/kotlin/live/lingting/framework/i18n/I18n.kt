package live.lingting.framework.i18n

import java.util.Collections
import java.util.Locale
import java.util.ServiceConfigurationError
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.Sequence
import live.lingting.framework.api.ApiI18nProviders
import live.lingting.framework.context.Context

/**
 * @author lingting 2024/11/30 18:26
 */
object I18n {

    @JvmStatic
    var defaultLocal: Locale = Locale.getDefault()

    @JvmField
    val providers: List<I18nProvider> = ArrayList<I18nProvider>().let {
        it.add(ApiI18nProviders())

        try {
            val loaders = ServiceLoader.load<I18nProvider>(I18nProvider::class.java)

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

    @JvmField
    val sourceMap: Map<Locale, List<I18nSource>> = HashMap<Locale, List<I18nSource>>().let {

        for (provider in providers) {
            for (source in provider.load()) {
                val locale = source.locale
                val absent = it.computeIfAbsent(locale) { ArrayList() }
                val list = absent.toMutableList()
                list.add(source)
                Sequence.asc(list)
                it[locale] = list.toList()
            }
        }

        Collections.unmodifiableMap(it)
    }

    @JvmField
    val localeMap = ConcurrentHashMap<Locale, I18nLocal>()

    /**
     * 对应语言的替代品, 用于国际化依次查找, 这样子不用重复设置相同值
     */
    @JvmField
    val replaceMap = ConcurrentHashMap<Locale, MutableList<Locale>>().apply {
        put(Locale.CHINESE, mutableListOf(Locale.SIMPLIFIED_CHINESE))
        put(Locale.ENGLISH, mutableListOf(Locale.US))
    }

    private val context = Context<Locale>({ defaultLocal })

    fun get() = context.get(defaultLocal)

    fun set(locale: Locale) = context.set(locale)

    fun remove() = context.remove()

    @JvmStatic
    @JvmOverloads
    fun local(locale: Locale = get()): I18nLocal {
        return localeMap.computeIfAbsent(locale) {
            val locales = LinkedHashSet<Locale>().apply {
                // 最准确的
                add(locale)
                // 匹配的次级选项
                add(Locale(locale.language))
                // 替代品
                val r = replaceMap[locale]
                if (!r.isNullOrEmpty()) {
                    addAll(r)
                }
            }

            val set = LinkedHashSet<I18nSource>()

            locales.forEach {
                val v = sourceMap[it]
                if (!v.isNullOrEmpty()) {
                    set.addAll(v)
                }
            }

            I18nLocal(locale, set.toList())
        }
    }

    @JvmStatic
    @JvmOverloads
    fun find(key: String, locale: Locale = get()): String? {
        val local = local(locale)
        return local.find(key)
    }

    @JvmStatic
    @JvmOverloads
    fun find(key: String, value: String, locale: Locale = get()): String {
        val local = local(locale)
        return local.find(key, value)
    }

}
