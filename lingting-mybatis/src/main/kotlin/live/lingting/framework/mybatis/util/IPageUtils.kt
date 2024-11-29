package live.lingting.framework.mybatis.util

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.metadata.OrderItem
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.api.PaginationResult
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024/11/22 19:01
 */
object IPageUtils {

    @JvmStatic
    fun <T> PaginationParams.toIPage() = Page<T>(page, size).apply {
        if (sorts.isEmpty()) {
            return@apply
        }

        for ((field, desc) in sorts) {
            val item = OrderItem()
            val isAsc = !desc
            val column = StringUtils.humpToUnderscore(field)
            item.isAsc = isAsc
            item.column = column
            addOrder(item)
        }
    }

    @JvmStatic
    fun <T> PaginationResult<T>.toIPage() = Page<T>().apply {
        total = this@toIPage.total
        records = this@toIPage.records
    }

    @JvmStatic
    fun <T> IPage<T>.toParams() = PaginationParams(current, size).apply {
        val orders = this@toParams.orders()
        if (orders.isEmpty()) {
            return@apply
        }
        val list = ArrayList<PaginationParams.Sort>(orders.size)
        orders.forEach {
            val column = it.column
            val asc = it.isAsc

            val sort = PaginationParams.Sort(column, !asc)
            list.add(sort)
        }
        sorts = list
    }

    @JvmStatic
    fun <T> IPage<T>.toResult() = PaginationResult(total, records)

}
