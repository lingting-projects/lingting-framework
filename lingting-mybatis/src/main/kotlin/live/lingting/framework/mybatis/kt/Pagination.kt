package live.lingting.framework.mybatis.kt

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.metadata.OrderItem
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.api.PaginationResult
import live.lingting.framework.util.CollectionUtils

/**
 * @author lingting 2024/11/22 19:01
 */
fun <T> PaginationParams.toIPage() = Page<T>(page, size).apply {
    if (!CollectionUtils.isEmpty(sorts)) {
        for ((field, desc) in sorts) {
            val item = OrderItem()
            item.isAsc = !desc
            item.column = field
            addOrder(item)
        }
    }
}

fun <T> PaginationResult<T>.toIPage() = Page<T>().apply {
    total = this@toIPage.total
    records = this@toIPage.records
}

fun <T> IPage<T>.toParams() = PaginationParams(current, size)

fun <T> IPage<T>.toResult() = PaginationResult(total, records)
