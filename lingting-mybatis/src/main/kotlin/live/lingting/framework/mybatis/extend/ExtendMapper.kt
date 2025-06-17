package live.lingting.framework.mybatis.extend

import com.baomidou.mybatisplus.core.conditions.Wrapper
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import live.lingting.framework.api.PaginationCursor
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.api.PaginationResult
import live.lingting.framework.mybatis.util.IPageUtils.toIPage
import live.lingting.framework.mybatis.util.IPageUtils.toParams
import live.lingting.framework.mybatis.util.IPageUtils.toResult
import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2022/9/26 17:07
 */
interface ExtendMapper<T> : BaseMapper<T> {

    fun insertIgnore(t: T): Int

    fun toIPage(params: PaginationParams): Page<T> = params.toIPage()

    fun <E> toIPage(result: PaginationResult<E>): Page<E> = result.toIPage()

    fun toParams(iPage: IPage<T>): PaginationParams = iPage.toParams()

    fun toResult(iPage: IPage<T>): PaginationResult<T> = iPage.toResult()

    fun selectPage(limit: PaginationParams, queryWrapper: Wrapper<T>?): PaginationResult<T> {
        val iPage = toIPage(limit)
        val tPage = selectPage(iPage, queryWrapper)
        return toResult(tPage)
    }

    fun cursor(params: PaginationParams): CursorValue<T> = cursor(params, Wrappers.emptyWrapper())

    fun cursor(params: PaginationParams, wrapper: Wrapper<T>): CursorValue<T> {
        return PaginationCursor<T>(params) { selectPage(it, wrapper) }
    }

}
