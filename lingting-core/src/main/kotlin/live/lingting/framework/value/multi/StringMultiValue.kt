package live.lingting.framework.value.multi


import java.util.Collections
import java.util.function.Supplier

/**
 * @author lingting 2024-09-14 11:21
 */
open class StringMultiValue : AbstractMultiValue<String, String, MutableCollection<String>> {
    constructor() : this(Supplier<MutableCollection<String>> { ArrayList() })

    protected constructor(supplier: Supplier<MutableCollection<String>>) : super(supplier)

    protected constructor(allowModify: Boolean, supplier: Supplier<MutableCollection<String>>) : super(allowModify, supplier)

    override fun unmodifiable(): StringMultiValue {
        val value = StringMultiValue(false, supplier)
        value.from(this) { Collections.unmodifiableCollection<String>(it) }
        return value
    }
}
