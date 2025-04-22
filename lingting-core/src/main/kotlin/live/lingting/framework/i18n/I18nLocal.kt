package live.lingting.framework.i18n

import java.util.Locale
import java.util.function.Supplier

/**
 * @author lingting 2024/11/30 18:26
 */
class I18nLocal(val local: Locale, val collection: List<I18nSource>) {

    fun find(key: String): String? {
        return find(key) { null }
    }

    fun find(key: String, onNotFound: Supplier<String?>): String? {
        for (source in collection) {
            val value = source.find(key)
            if (value != null) {
                return value
            }
        }
        return onNotFound.get()
    }

    fun find(key: String, value: String): String {
        return find(key) ?: value
    }

}
