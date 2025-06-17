package live.lingting.framework.http.header

import live.lingting.framework.value.MultiValue
import live.lingting.framework.value.multi.StringMultiValue
import java.util.function.Supplier

/**
 * @author lingting 2024-09-13 11:15
 */
abstract class AbstractHttpHeaders : StringMultiValue, HttpHeaders {

    protected constructor(supplier: Supplier<MutableCollection<String>>) : super(supplier)

    protected constructor(allowModify: Boolean, supplier: Supplier<MutableCollection<String>>) :
            super(allowModify, supplier)

    override fun convert(key: String): String {
        return key.lowercase()
    }

    override fun unmodifiable(): UnmodifiableHttpHeaders {
        return UnmodifiableHttpHeaders(this)
    }

    override fun hasKey(key: String): Boolean {
        return map[key]?.isEmpty() == false
    }

    override fun keys(): Set<String> {
        return entries().map { it.key }.toSet()
    }

    override fun values(): Collection<MutableCollection<String>> {
        return entries().map { it.value }
    }

    override fun entries(): List<MultiValue.Entry<String, String, MutableCollection<String>>> {
        return super.entries().filter { it.value.isNotEmpty() }
    }

}
