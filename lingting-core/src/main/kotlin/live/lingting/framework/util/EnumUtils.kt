package live.lingting.framework.util

import com.baomidou.mybatisplus.annotation.IEnum
import com.fasterxml.jackson.annotation.JsonValue
import live.lingting.framework.reflect.ClassField
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2022/12/20 14:52
 */
class EnumUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val METHOD_GET_VALUE: String = "getValue"

        const val CLS_MYBATIS_PLUS_IENUM: String = "com.baomidou.mybatisplus.annotation.IEnum"

        const val CLS_JACKSON_JSON_VALUE: String = "com.fasterxml.jackson.annotation.JsonValue"

        val CACHE: MutableMap<Class<*>, ClassField?> = ConcurrentHashMap()

        fun getByIEnum(cls: Class<*>): ClassField? {
            if (!ClassUtils.isPresent(CLS_MYBATIS_PLUS_IENUM, EnumUtils::class.java.getClassLoader())) {
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

        fun getByJsonValue(cls: Class<*>): ClassField? {
            if (!ClassUtils.isPresent(CLS_JACKSON_JSON_VALUE, EnumUtils::class.java.getClassLoader())) {
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

        fun getJsonValueMethod(cls: Class<*>): Method? {
            // 获取public的方法.
            val methods = cls.methods
            for (method in methods) {
                val annotation: Annotation? = method.getAnnotation<JsonValue>(JsonValue::class.java)
                // 存在注解且参数为空
                if (annotation != null && ArrayUtils.isEmpty<Parameter>(method.parameters)) {
                    return method
                }
            }
            return null
        }

        fun getJsonValueField(cls: Class<*>): Field? {
            val fields = cls.declaredFields

            for (field in fields) {
                val annotation: Annotation? = field.getAnnotation<JsonValue>(JsonValue::class.java)
                if (annotation != null) {
                    return field
                }
            }
            return null
        }

        fun getByName(cls: Class<*>?): ClassField? {
            val method: Method = ClassUtils.method(cls, "name")
            if (method != null) {
                return ClassField(null, method, null)
            }
            return null
        }

        fun getCf(cls: Class<*>): ClassField? {
            return CACHE.computeIfAbsent(cls) { k: Class<*>? ->
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

        fun <E : Enum<E>?> getValue(e: Enum<E>?): Any? {
            if (e == null) {
                return null
            }
            val cf = getCf(e.javaClass)

            try {
                if (cf!!.canGet(e)) {
                    return cf[e]
                }
                return cf.visibleGet()[e]
            } catch (ex: Exception) {
                return null
            }
        }
    }
}
