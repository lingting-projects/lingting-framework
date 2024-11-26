package live.lingting.framework.api

/**
 * @author lingting 2024-02-02 17:53
 */
class PaginationParams(
    var page: Long,
    var size: Long,
    var sorts: List<Sort>,
) {

    constructor() : this(1, 10, emptyList())

    constructor(page: Long, size: Long) : this(page, size, emptyList())

    /**
     * 数据起始索引
     */
    fun start(): Long {
        return (page - 1) * size
    }

    data class Sort(

        /**
         * 排序字段
         */
        var field: String,

        /**
         * 是否倒序
         */
        var desc: Boolean,
    ) {

        constructor() : this("", false)

    }
}
