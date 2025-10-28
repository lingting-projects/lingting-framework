package live.lingting.framework.reflect

import live.lingting.framework.util.AnnotationUtils.findAnnotation
import live.lingting.framework.util.ClassUtils
import live.lingting.framework.util.ClassUtils.toFiledName
import live.lingting.framework.util.FieldUtils.isFinal
import live.lingting.framework.util.StringUtils.firstUpper
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 用于获取指定字段的值
 * 优先取指定字段的 get 方法
 * 如果是 boolean 类型, 尝试取 is 方法
 * 否则直接取字段 - 不会尝试修改可读性, 如果可读性有问题, 请主动get 然后修改
 * @author lingting 2022/12/6 13:04
 */

class ClassField @JvmOverloads constructor(
    val name: String, val cls: Class<*>,
    f: Field? = null, g: Method? = null, s: Method? = null
) {

    companion object {

        @JvmStatic
        fun findName(m: Method?): String? {
            val name = m?.name
            if (name.isNullOrBlank()) {
                return null
            }
            return toFiledName(name)
        }

        @JvmStatic
        fun findName(f: Field? = null, g: Method? = null, s: Method? = null): String {
            return f?.name ?: findName(g) ?: findName(s) ?: ""
        }

        @JvmStatic
        fun findClass(f: Field? = null, g: Method? = null, s: Method? = null): Class<*> {
            return f?.type ?: g?.returnType ?: s!!.parameterTypes[0]
        }
    }

    constructor(f: Field? = null, g: Method? = null, s: Method? = null) : this(
        findName(f, g, s), findClass(f, g, s),
        f, g, s
    )

    val field: Field? by lazy { f ?: ClassUtils.field(cls, name) }
    val methodGet: Method? by lazy {
        g ?: ClassUtils.method(cls, "get${name.firstUpper()}") ?: ClassUtils.method(cls, "is${name.firstUpper()}")
    }
    val methodSet: Method? by lazy {
        s ?: ClassUtils.method(cls, "set${name.firstUpper()}")
    }

    val valueType: Class<*> by lazy { field?.type ?: (methodGet?.returnType ?: methodSet!!.parameterTypes[0]) }

    val hasField by lazy { field != null }

    val isFinalField by lazy { field?.isFinal == true }

    /**
     * 是否拥有指定注解, 会同时对 字段 和 方法进行判断
     * @param a 注解类型
     * @return boolean true 表示拥有
     */
    fun <T : Annotation> hasAnnotation(a: Class<T>): Boolean {
        return getAnnotation(a) != null
    }

    fun <T : Annotation> getAnnotation(a: Class<T>): T? {
        // 字段上找
        var annotation = getAnnotation(field, a)
        // 方法上找
        if (annotation == null) {
            annotation = getAnnotation(methodGet, a)
        }
        if (annotation == null) {
            annotation = getAnnotation(methodSet, a)
        }
        return annotation
    }

    fun <T : Annotation> getAnnotation(obj: AccessibleObject?, a: Class<T>): T? {
        if (obj == null) {
            return null
        }
        return findAnnotation(obj, a)
    }

    /**
     * 获取字段值, 仅支持无参方法
     * @param obj 对象
     * @return java.lang.Object 对象指定字段值
     */
    fun get(obj: Any): Any? {
        val get = methodGet
        if (get != null) {
            return get.invoke(obj)
        }
        return field!![obj]
    }

    /**
     * 设置字段值
     * @param obj 对象
     * @param args set方法参数, 如果无set方法, 则第一个参数会被作为值通过字段设置
     */
    fun set(obj: Any?, vararg args: Any?) {
        val set = methodSet
        if (set != null) {
            set.invoke(obj, *args)
            return
        }
        field!![obj] = args[0]
    }

    // region visible

    /**
     * 是否能够获取值
     */
    fun canGet(o: Any): Boolean {
        val get = methodGet
        if (get != null) {
            return get.canAccess(o)
        }
        val f = field
        if (f != null) {
            return f.canAccess(o)
        }
        return false
    }

    /**
     * 是否能够设置值
     */
    fun canSet(o: Any): Boolean {
        val set = methodSet
        if (set == null) {
            return false
        }
        val f = field
        if (f != null) {
            return f.canAccess(o) && !f.isFinal
        }
        return set.canAccess(o)
    }

    /**
     * 将方法转化为可见的
     * 未声明可能会调用异常. [相关回答](https://stackoverflow.com/a/71296829/19334734)
     */
    fun visible(): ClassField {
        return visibleSet().visibleGet()
    }

    fun visibleGet(): ClassField {
        val f = field
        if (f != null && !f.trySetAccessible()) {
            f.isAccessible = true
        }
        val get = methodGet
        if (get != null && !get.trySetAccessible()) {
            get.isAccessible = true
        }
        return this
    }

    fun visibleSet(): ClassField {
        val f = field
        if (f != null && !f.trySetAccessible()) {
            f.isAccessible = true
        }
        val set = methodSet
        if (set != null && !set.trySetAccessible()) {
            set.isAccessible = true
        }
        return this
    }

    // endregion

}
