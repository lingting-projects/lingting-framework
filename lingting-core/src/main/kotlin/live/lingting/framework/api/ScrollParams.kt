package live.lingting.framework.api

/**
 * @author lingting 2024-02-02 17:54
 */
data class ScrollParams<T>(
    var size: Long,
    var cursor: T?,
) {

    constructor() : this(10, null)

}
