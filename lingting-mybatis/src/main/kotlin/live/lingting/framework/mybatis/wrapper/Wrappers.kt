package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import live.lingting.framework.reflect.LambdaMeta
import live.lingting.framework.util.StringUtils

@Suppress("UNCHECKED_CAST")
object Wrappers {
    const val PARAM = Constants.WRAPPER

    private val COLUMN_CACHE = ConcurrentHashMap<Class<*>, MutableMap<String, ColumnCache>>()

    fun extract(sf: SFunction<*, *>): LambdaMeta {
        return LambdaMeta.of(sf)
    }

    @JvmStatic
    fun column(cls: Class<*>): MutableMap<String, ColumnCache> {
        return COLUMN_CACHE.computeIfAbsent(cls) { LambdaUtils.getColumnMap(it) ?: mutableMapOf() }
    }

    @JvmStatic
    fun column(sf: SFunction<*, *>): ColumnCache {
        val info = extract(sf)
        return column(info.cls, info.field)
    }

    @JvmStatic
    fun <T> column(cls: Class<T>, sf: SFunction<T, *>): ColumnCache {
        val info = extract(sf)
        return column(cls, info.field)
    }

    @JvmStatic
    fun column(cls: Class<*>?, field: String): ColumnCache {
        val clazz = cls ?: Wrappers::class.java
        val map = column(clazz)
        val key = LambdaUtils.formatKey(field)
        val v1 = map[key]
        if (v1 != null) {
            return v1
        }
        // 转驼峰
        val hump = StringUtils.underscoreToHump(field)
        // 不匹配重新获取
        if (!Objects.equals(field, hump)) {
            return column(clazz, hump)
        }
        // 没有手动生成
        val underscore = StringUtils.humpToUnderscore(field)
        val cache = ColumnCache(underscore, underscore)
        map[key] = cache
        return cache
    }

    @JvmStatic
    fun <T : Any> query(): QueryWrapper<T> {
        return QueryWrapper<T>()
    }

    @JvmStatic
    fun <T : Any> query(cls: Class<T>): QueryWrapper<T> {
        return QueryWrapper<T>().cls(cls)
    }

    fun <T : Any> query(cls: KClass<T>) = query(cls.java)

    @JvmStatic
    fun <T : Any> query(t: T?): QueryWrapper<T> {
        return QueryWrapper<T>().apply {
            e = t
            if (t != null) {
                cls(t::class.java as Class<T>)
            }
        }
    }

    @JvmStatic
    fun <T : Any> update(): UpdateWrapper<T> {
        return UpdateWrapper<T>()
    }

    @JvmStatic
    fun <T : Any> update(cls: Class<T>): UpdateWrapper<T> {
        return UpdateWrapper<T>().cls(cls)
    }

    fun <T : Any> update(cls: KClass<T>) = update(cls.java)

    @JvmStatic
    fun <T : Any> update(t: T?): UpdateWrapper<T> {
        return UpdateWrapper<T>().apply {
            e = t
            if (t != null) {
                cls(t::class.java as Class<T>)
            }
        }
    }

}
