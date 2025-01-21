package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.support.SFunction

@Suppress("SYNTHETIC_PROPERTY_WITHOUT_JAVA_ORIGIN")
class QueryWrapper<T : Any> : LambdaWrapper<T, QueryWrapper<T>>() {
    override fun instance(): QueryWrapper<T> {
        val w = QueryWrapper<T>()
        w.paramId = paramId
        return w
    }

    override fun getSafeSql(): String {
        val builder = StringBuilder()

        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(first)) {
            builder.append(first)
        }

        builder.append("SELECT ")

        if (fields.isEmpty) {
            builder.append("*")
        } else {
            builder.append(fields.joinToString(", "))
        }

        val info: com.baomidou.mybatisplus.core.metadata.TableInfo = TableInfoHelper.getTableInfo(entityClass)

        builder.append(" FROM ").append(info.tableName).append(" ")

        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(alias)) {
            builder.append(alias).append(" ")
        }

        val segment = segments.sqlSegment

        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(segment)) {
            builder.append("WHERE ").append(segment)
        }

        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(last)) {
            builder.append(last)
        }

        return builder.toString()
    }

    // region select
    fun select(vararg args: String): QueryWrapper<T> {
        return select(args.toList())
    }

    @SafeVarargs
    fun select(vararg args: SFunction<T, *>): QueryWrapper<T> {
        val fields = args.map { sf -> this.field(sf) }.toList()
        return select(fields)
    }

    fun select(source: Collection<String>): QueryWrapper<T> {
        for (field in source) {
            val s = field(field)
            fields.add(s)
        }
        return c
    }

// endregion

}
