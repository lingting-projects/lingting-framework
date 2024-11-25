package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta
import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import java.util.concurrent.ConcurrentHashMap
import org.apache.ibatis.reflection.property.PropertyNamer

@Suppress("UNCHECKED_CAST")
object Wrappers {
    const val PARAM = Constants.WRAPPER

    private val COLUMN_CACHE = ConcurrentHashMap<Class<*>, MutableMap<String, ColumnCache>>()

    private val COLUMN_SF_CACHE = ConcurrentHashMap<SFunction<*, *>, ColumnCache?>()

    @JvmStatic
    fun column(cls: Class<*>): MutableMap<String, ColumnCache> {
        return COLUMN_CACHE.computeIfAbsent(cls) { k: Class<*> -> LambdaUtils.getColumnMap(cls) }
    }

    @JvmStatic
    fun column(sf: SFunction<*, *>): ColumnCache? {
        return COLUMN_SF_CACHE.computeIfAbsent(sf) { sk: SFunction<*, *> ->
            val meta: LambdaMeta = LambdaUtils.extract(sf)
            val field: String = PropertyNamer.methodToProperty(meta.implMethodName)
            val cls: Class<*> = meta.instantiatedClass
            column(cls, field)
        }
    }

    @JvmStatic
    fun <T> column(cls: Class<T>, sf: SFunction<T, *>): ColumnCache? {
        return COLUMN_SF_CACHE.computeIfAbsent(sf) { sk: SFunction<*, *> ->
            val meta: LambdaMeta = LambdaUtils.extract<T>(sf)
            val field: String = PropertyNamer.methodToProperty(meta.implMethodName)
            column(cls, field)
        }
    }

    @JvmStatic
    fun column(cls: Class<*>, field: String): ColumnCache? {
        val map: MutableMap<String, ColumnCache> = column(cls)
        val key: String = LambdaUtils.formatKey(field)
        return map[key]
    }

    @JvmStatic
    fun <T> query(): QueryWrapper<T> {
        return QueryWrapper<T>()
    }

    @JvmStatic
    fun <T> query(cls: Class<T>): QueryWrapper<T> {
        return QueryWrapper<T>().cls(cls)
    }

    @JvmStatic
    fun <T> query(t: T?): QueryWrapper<T> {
        return QueryWrapper<T>().apply {
            e = t
            if (t != null) {
                cls(t::class.java as Class<T>)
            }
        }
    }

    @JvmStatic
    fun <T> update(): UpdateWrapper<T> {
        return UpdateWrapper<T>()
    }

    @JvmStatic
    fun <T> update(cls: Class<T>): UpdateWrapper<T> {
        return UpdateWrapper<T>().cls(cls)
    }

    @JvmStatic
    fun <T> update(t: T): UpdateWrapper<T> {
        return UpdateWrapper<T>().apply {
            e = t
            if (t != null) {
                cls(t::class.java as Class<T>)
            }
        }
    }

}
