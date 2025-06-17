package live.lingting.framework.api

/**
 * @author lingting 2024-02-02 17:54
 */
data class ScrollParams<T>(
    var size: Long,
    var cursor: T? = null,
) {

    constructor() : this(10)

    constructor(size: Long) : this(size, null)

}
