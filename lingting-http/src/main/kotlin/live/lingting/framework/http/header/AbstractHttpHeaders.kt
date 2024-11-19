package live.lingting.framework.http.header

import java.util.function.Supplier
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2024-09-13 11:15
 */
abstract class AbstractHttpHeaders : StringMultiValue, HttpHeaders {

    protected constructor(supplier: Supplier<MutableCollection<String>>) : super(supplier)

    protected constructor(allowModify: Boolean, supplier: Supplier<MutableCollection<String>>) : super(allowModify, supplier)

    override fun convert(key: String): String {
        return key.lowercase()
    }

    override fun unmodifiable(): UnmodifiableHttpHeaders {
        return UnmodifiableHttpHeaders(this)
    }
}
