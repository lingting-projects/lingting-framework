package live.lingting.framework.http.header

import live.lingting.framework.value.multi.StringMultiValue
import java.util.*
import java.util.function.Supplier

/**
 * @author lingting 2024-09-13 11:15
 */
abstract class AbstractHttpHeaders : StringMultiValue, HttpHeaders {
    protected constructor(supplier: Supplier<Collection<String>>) : super(supplier)

    protected constructor(allowModify: Boolean, supplier: Supplier<Collection<String>>) : super(allowModify, supplier)

    override fun convert(key: String): String {
        return key.lowercase(Locale.getDefault())
    }

    override fun unmodifiable(): UnmodifiableHttpHeaders {
        return UnmodifiableHttpHeaders(this)
    }
}
