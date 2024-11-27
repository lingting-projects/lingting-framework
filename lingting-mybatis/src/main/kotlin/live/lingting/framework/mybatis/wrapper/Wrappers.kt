package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.toolkit.Constants
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import com.baomidou.mybatisplus.core.toolkit.support.ReflectLambdaMeta
import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda
import com.baomidou.mybatisplus.core.toolkit.support.ShadowLambdaMeta
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import live.lingting.framework.mybatis.lambda.LambdaInfo
import live.lingting.framework.util.ClassUtils
import org.apache.ibatis.reflection.ReflectionException

@Suppress("UNCHECKED_CAST")
object Wrappers {
    const val PARAM = Constants.WRAPPER

    private val COLUMN_CACHE = ConcurrentHashMap<Class<*>, MutableMap<String, ColumnCache>>()

    private val COLUMN_SF_CACHE = ConcurrentHashMap<SFunction<*, *>, ColumnCache?>()

    fun extract(sf: SFunction<*, *>): LambdaInfo {
        return if (sf is Proxy) {
            LambdaInfo.of(sf)
        } else {
            try {
                val cls = sf.javaClass
                val loader = cls.classLoader
                val cf = ClassUtils.method(cls, "writeReplace")!!.apply { isAccessible = true }
                val lambda = cf.invoke(sf) as java.lang.invoke.SerializedLambda

                try {
                    val meta = ReflectLambdaMeta(lambda, loader)
                    LambdaInfo.of(meta)
                } catch (_: ReflectionException) {
                    LambdaInfo.of(lambda, loader)
                }

            } catch (_: Throwable) {
                val extract = SerializedLambda.extract(sf)
                val meta = ShadowLambdaMeta(extract)
                LambdaInfo.of(meta)
            }
        }
    }

    @JvmStatic
    fun column(cls: Class<*>): MutableMap<String, ColumnCache> {
        return COLUMN_CACHE.computeIfAbsent(cls) { k: Class<*> -> LambdaUtils.getColumnMap(cls) }
    }

    @JvmStatic
    fun column(sf: SFunction<*, *>): ColumnCache? {
        return COLUMN_SF_CACHE.computeIfAbsent(sf) {
            val info = extract(sf)
            column(info.cls, info.field)
        }
    }

    @JvmStatic
    fun <T> column(cls: Class<T>, sf: SFunction<T, *>): ColumnCache? {
        return COLUMN_SF_CACHE.computeIfAbsent(sf) { sk ->
            val info = extract(sf)
            column(cls, info.field)
        }
    }

    @JvmStatic
    fun column(cls: Class<*>, field: String): ColumnCache? {
        val map: MutableMap<String, ColumnCache> = column(cls)
        val key: String = LambdaUtils.formatKey(field)
        return map[key]
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
