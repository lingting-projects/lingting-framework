package live.lingting.framework.http.header

import live.lingting.framework.value.multi.AbstractMultiValue
import java.util.*
import java.util.function.Supplier

/**
 * @author lingting 2024-09-12 23:45
 */
open class UnmodifiableHttpHeaders(value: AbstractMultiValue<String?, String?, *>) : AbstractHttpHeaders(false, Supplier<Collection<String>> { ArrayList() }), HttpHeaders {
    init {
        from(value) { c: Collection<T?>? -> Collections.unmodifiableCollection(c) }
    }

    override fun unmodifiable(): UnmodifiableHttpHeaders {
        return this
    }
}
