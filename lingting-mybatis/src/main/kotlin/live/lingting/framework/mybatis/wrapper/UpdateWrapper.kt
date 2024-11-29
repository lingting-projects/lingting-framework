package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.toolkit.support.SFunction

class UpdateWrapper<T : Any> : LambdaWrapper<T, UpdateWrapper<T>>(), com.baomidou.mybatisplus.core.conditions.update.Update<UpdateWrapper<T>, String> {

    private val sets = ArrayList<String>()

    override fun instance(): UpdateWrapper<T> {
        return UpdateWrapper<T>()
    }

    override fun getSafeSql(): String {
        throw UnsupportedOperationException()
    }

    override fun set(condition: Boolean, column: String, value: Any?, mapping: String?): UpdateWrapper<T> {
        if (!condition) {
            return this
        }
        val name = convertField(column)
        val param = safeParam(mapping, value)
        return setSql(true, "$name=$param")
    }

    override fun setSql(condition: Boolean, setSql: String, vararg params: Any?): UpdateWrapper<T> {
        if (condition) {
            val sql = formatSqlByParam(setSql, params)
            if (!sql.isNullOrBlank()) {
                sets.add(sql)
            }
        }
        return this
    }

    @JvmOverloads
    fun setSql(column: String, setSql: String, vararg params: Any? = arrayOf()): UpdateWrapper<T> {
        val name = convertField(column)
        val format = formatSqlByParam(setSql, params)
        val sql = "$name $format"
        sets.add(sql)
        return this
    }


    // region ifPresent

    fun setIfPresent(column: String, value: Any?): UpdateWrapper<T> {
        return set(isPresent(value), column, value)
    }

    fun setIfPresent(column: String, value: Any?, mapping: String?): UpdateWrapper<T> {
        return set(isPresent(value), column, value, mapping)
    }

    // endregion

    // region lambda

    fun <V : Any> set(column: SFunction<T, V?>, value: V?): UpdateWrapper<T> {
        return set(true, column, value)
    }

    fun <V : Any> set(condition: Boolean, column: SFunction<T, V?>, value: V?): UpdateWrapper<T> {
        return set(condition, column, value, null)
    }

    fun <V : Any> set(column: SFunction<T, V?>, value: V?, mapping: String?): UpdateWrapper<T> {
        return set(true, column, value, mapping)
    }

    fun <V : Any> set(condition: Boolean, column: SFunction<T, V?>, value: V?, mapping: String?): UpdateWrapper<T> {
        if (!condition) {
            return this
        }
        val field = convertField(column)
        return set(true, field, value, mapping)
    }

    @JvmOverloads
    fun setSql(column: SFunction<T, *>, setSql: String, vararg params: Any? = arrayOf()): UpdateWrapper<T> {
        val field = convertField(column)
        return setSql(true, field, setSql, *params)
    }

    // endregion

    // region lambdaIfPresent

    fun <V : Any> setIfPresent(column: SFunction<T, V?>, value: V?): UpdateWrapper<T> {
        return set(isPresent(value), column, value)
    }

    fun <V : Any> setIfPresent(column: SFunction<T, V?>, value: V?, mapping: String?): UpdateWrapper<T> {
        return set(isPresent(value), column, value, mapping)
    }

    // endregion

    fun setSql(condition: Boolean, sql: String): UpdateWrapper<T> {
        if (condition) {
            sets.add(sql)
        }
        return this
    }

    fun countSet(): Int {
        return sets.size
    }

    override fun getSqlSet(): String? {
        if (sets.isEmpty()) {
            return null
        }
        return sets.joinToString(", ")
    }

    override fun clear() {
        super.clear()
        sets.clear()
    }

}
