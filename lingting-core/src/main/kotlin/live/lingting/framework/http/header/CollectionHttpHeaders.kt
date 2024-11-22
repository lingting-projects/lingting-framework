package live.lingting.framework.http.header

import java.util.function.Supplier

/**
 * @author lingting 2024-09-12 23:41
 */
class CollectionHttpHeaders : AbstractHttpHeaders, HttpHeaders {

    @JvmOverloads
    constructor(allowModify: Boolean = true, supplier: Supplier<MutableCollection<String>> = Supplier { ArrayList() }) : super(allowModify, supplier)

}
