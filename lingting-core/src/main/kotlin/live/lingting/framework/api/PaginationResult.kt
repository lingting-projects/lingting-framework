package live.lingting.framework.api

/**
 * @author lingting 2024-02-02 17:53
 */
data class PaginationResult<T>(var total: Long, var records: List<T>) {

    constructor() : this(0, emptyList())

}
