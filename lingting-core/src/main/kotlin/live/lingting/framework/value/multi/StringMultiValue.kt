package live.lingting.framework.value.multi

import java.util.*
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author lingting 2024-09-14 11:21
 */
open class StringMultiValue : AbstractMultiValue<String, String, Collection<String>> {
    constructor() : this(Supplier<Collection<String>> { ArrayList() })

    protected constructor(supplier: Supplier<Collection<String>>) : super(supplier)

    protected constructor(allowModify: Boolean, supplier: Supplier<Collection<String>>) : super(allowModify, supplier)

    override fun unmodifiable(): StringMultiValue {
        val value = StringMultiValue(false, supplier)
        value.from<Collection<String>>(this, Function<Collection<String>, Collection<String>> { c: Collection<String> -> Collections.unmodifiableCollection(c) })
        return value
    }
}
