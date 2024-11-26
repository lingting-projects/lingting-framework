package live.lingting.framework.mybatis.wrapper

import java.lang.UnsupportedOperationException
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils

class UpdateWrapper<T> : LambdaWrapper<T, UpdateWrapper<T>>(), com.baomidou.mybatisplus.core.conditions.update.Update<UpdateWrapper<T>, String> {
    private val sets: MutableList<String> = java.util.ArrayList<String>()

    override fun instance(): UpdateWrapper<T> {
        return UpdateWrapper<T>()
    }

    override fun getSafeSql(): String {
        throw UnsupportedOperationException()
    }

    override fun set(condition: Boolean, column: String, `val`: Any, mapping: String): UpdateWrapper<T> {
        if (!condition) {
            return this
        }
        val name = convertField(column)
        val param = safeParam(`val`)
        return setSql(true, "$name=$param")
    }

    override fun setSql(condition: Boolean, setSql: String, vararg params: Any?): UpdateWrapper<T> {
        if (condition) {
            val sql = formatSqlByParam(setSql, params)
            if (StringUtils.hasText(sql)) {
                sets.add(sql!!)
            }
        }
        return this
    }

    fun setSql(condition: Boolean, sql: String): UpdateWrapper<T> {
        if (condition) {
            sets.add(sql)
        }
        return this
    }

    override fun getSqlSet(): String? {
        if (CollectionUtils.isEmpty(sets)) {
            return null
        }
        return sets.joinToString(", ")
    }

    override fun clear() {
        super.clear()
        sets.clear()
    }

}
