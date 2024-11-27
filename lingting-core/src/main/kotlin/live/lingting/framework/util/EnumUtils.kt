package live.lingting.framework.util

import com.baomidou.mybatisplus.annotation.IEnum
import com.fasterxml.jackson.annotation.JsonValue
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.reflect.ClassField

/**
 * @author lingting 2022/12/20 14:52
 */
object EnumUtils {
    const val METHOD_GET_VALUE: String = "getValue"

    const val CLS_MYBATIS_PLUS_IENUM: String = "com.baomidou.mybatisplus.annotation.IEnum"

    const val CLS_JACKSON_JSON_VALUE: String = "com.fasterxml.jackson.annotation.JsonValue"

    val CACHE: MutableMap<Class<*>, ClassField?> = ConcurrentHashMap()

    @JvmStatic
    fun getByIEnum(cls: Class<*>): ClassField? {
        if (!ClassUtils.exists(CLS_MYBATIS_PLUS_IENUM, EnumUtils::class.java.getClassLoader())) {
            return null
        }

        var method: Method? = null
        if (IEnum::class.java.isAssignableFrom(cls)) {
            method = ClassUtils.method(cls, METHOD_GET_VALUE)
        }

        if (method == null) {
            return null
        }
        return ClassField(null, method, null)
    }

    @JvmStatic
    fun getByJsonValue(cls: Class<*>): ClassField? {
        if (!ClassUtils.exists(CLS_JACKSON_JSON_VALUE, EnumUtils::class.java.getClassLoader())) {
            return null
        }

        var method: Method? = null
        val field = getJsonValueField(cls)
        if (field != null) {
            // public 字段
            if (Modifier.isPublic(field.modifiers)) {
                return ClassField(field, null, null)
            }

            val name = "get" + StringUtils.firstUpper(field.name)
            // 获取 get 方法
            method = ClassUtils.method(cls, name)
            if (method != null) {
                return ClassField(null, method, null)
            }
        }

        method = getJsonValueMethod(cls)
        if (method != null) {
            return ClassField(null, method, null)
        }
        return null
    }

    @JvmStatic
    fun getJsonValueMethod(cls: Class<*>): Method? {
        // 获取public的方法.
        val methods = cls.methods
        for (method in methods) {
            val annotation = method.getAnnotation<JsonValue>(JsonValue::class.java)
            // 存在注解且参数为空
            if (annotation != null && method.parameters.isEmpty()) {
                return method
            }
        }
        return null
    }

    @JvmStatic
    fun getJsonValueField(cls: Class<*>): Field? {
        val fields = cls.declaredFields

        for (field in fields) {
            val annotation = field.getAnnotation<JsonValue>(JsonValue::class.java)
            if (annotation != null) {
                return field
            }
        }
        return null
    }

    @JvmStatic
    fun getByName(cls: Class<*>): ClassField? {
        val method = ClassUtils.method(cls, "name")
        if (method != null) {
            return ClassField(null, method, null)
        }
        return null
    }

    @JvmStatic
    fun getCf(cls: Class<*>): ClassField? {
        return CACHE.computeIfAbsent(cls) { k: Class<*> ->
            var cf = getByIEnum(cls)
            if (cf == null) {
                cf = getByJsonValue(cls)
            }

            if (cf == null) {
                cf = getByName(cls)
            }
            cf
        }
    }

    @JvmStatic
    fun <E : Enum<E>> getValue(e: Enum<E>?): Any? {
        if (e == null) {
            return null
        }
        val cf = getCf(e.javaClass)

        if (cf == null) {
            return null;
        }

        try {
            if (cf.canGet(e)) {
                return cf.get(e)
            }
            return cf.visibleGet().get(e)
        } catch (_: Exception) {
            return null
        }
    }
}

