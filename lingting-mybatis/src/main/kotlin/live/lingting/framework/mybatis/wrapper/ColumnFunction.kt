package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.toolkit.support.SFunction

/**
 * 连表查询时，从其他表获取的查询条件
 *
 * @author hccake
 */
interface ColumnFunction<T> : SFunction<T, String?> {
    companion object {
        /**
         * 快捷的创建一个 ColumnFunction
         * @param columnString 实际的 column
         * @return ColumnFunction
         */
        fun <T> create(columnString: String?): ColumnFunction<T> {
            return ColumnFunction<T> { o: T -> columnString }
        }
    }
}
