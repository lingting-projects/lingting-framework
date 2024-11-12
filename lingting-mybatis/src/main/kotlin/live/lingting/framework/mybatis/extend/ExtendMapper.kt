package live.lingting.framework.mybatis.extend

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.metadata.OrderItem
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.api.PaginationResult
import live.lingting.framework.util.CollectionUtils

/**
 * @author lingting 2022/9/26 17:07
 */
interface ExtendMapper<T> : BaseMapper<T> {
    fun toIpage(params: PaginationParams): Page<T> {
        val page = Page<T>()
        page.setCurrent(params.page)
        page.setSize(params.size)

        val sorts = params.sorts
        if (!CollectionUtils.isEmpty(sorts)) {
            val orders = ArrayList<OrderItem>()

            for ((field, desc) in sorts) {
                val item = OrderItem()
                item.setAsc(!desc)
                item.setColumn(field)
                orders.add(item)
            }

            page.setOrders(orders)
        }

        return page
    }

    fun convert(iPage: IPage<T>): PaginationResult<T> {
        return PaginationResult(iPage.total, iPage.records)
    }

    fun selectPage(limit: PaginationParams, queryWrapper: Wrapper<T>?): PaginationResult<T> {
        val iPage = toIpage(limit)
        val tPage = selectPage(iPage, queryWrapper)
        return convert(tPage)
    }
}
