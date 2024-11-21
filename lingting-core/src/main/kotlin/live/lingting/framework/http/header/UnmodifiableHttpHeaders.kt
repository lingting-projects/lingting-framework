package live.lingting.framework.http.header

import java.util.Collections
import java.util.function.Supplier
import live.lingting.framework.value.multi.AbstractMultiValue

/**
 * @author lingting 2024-09-12 23:45
 */
open class UnmodifiableHttpHeaders(value: AbstractMultiValue<String, String, *>) :
    AbstractHttpHeaders(false, Supplier { ArrayList() }), HttpHeaders {
    init {
        from(value) { Collections.unmodifiableCollection(it) }
    }

    override fun unmodifiable(): UnmodifiableHttpHeaders {
        return this
    }

}
