package live.lingting.framework.reflect

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import live.lingting.framework.util.AnnotationUtils
import live.lingting.framework.util.ClassUtils.Companion.toFiledName

/**
 * 用于获取指定字段的值
 *
 *
 * 优先取指定字段的 get 方法
 *
 *
 *
 * 如果是 boolean 类型, 尝试取 is 方法
 *
 *
 *
 * 否则直接取字段 - 不会尝试修改可读性, 如果可读性有问题, 请主动get 然后修改
 *
 *
 * @author lingting 2022/12/6 13:04
 */

data class ClassField(val field: Field?, val methodGet: Method?, val methodSet: Method?) {

    val name: String = field?.name ?: when {
        methodGet != null -> toFiledName(methodGet.name)
        methodSet != null -> toFiledName(methodSet.name)
        else -> ""
    }

    val valueType: Class<*> = field?.type ?: methodGet!!.returnType

    val hasField = field != null

    val isFinalField = field != null && Modifier.isFinal(field.modifiers)

    /**
     * 是否拥有指定注解, 会同时对 字段 和 方法进行判断
     * @param a 注解类型
     * @return boolean true 表示拥有
     */
    fun <T : Annotation?> getAnnotation(a: Class<T>): T? {
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

    fun <T : Annotation?> getAnnotation(`object`: AccessibleObject?, a: Class<T>): T? {
        return if (`object` == null) null else AnnotationUtils.findAnnotation<T>(`object`, a)
    }

    /**
     * 获取字段值, 仅支持无参方法
     * @param obj 对象
     * @return java.lang.Object 对象指定字段值
     */
    fun get(obj: Any?): Any {
        if (methodGet != null) {
            return methodGet.invoke(obj)
        }
        return field!![obj]
    }

    /**
     * 设置字段值
     * @param obj 对象
     * @param args set方法参数, 如果无set方法, 则第一个参数会被作为值通过字段设置
     */
    fun set(obj: Any?, vararg args: Any?) {
        if (methodSet != null) {
            methodSet.invoke(obj, *args)
            return
        }
        field!![obj] = args[0]
    }

    // region visible

    /**
     * 是否能够获取值
     */
    fun canGet(o: Any?): Boolean {
        if (methodGet != null) {
            return methodGet.canAccess(o)
        }
        if (field != null) {
            return field.canAccess(o)
        }
        return false
    }

    /**
     * 是否能够设置值
     */
    fun canSet(o: Any?): Boolean {
        if (methodSet == null) {
            return false
        }
        return methodSet.canAccess(o)
    }

    /**
     * 将方法转化为可见的
     *
     *
     * 未声明可能会调用异常. [相关回答](https://stackoverflow.com/a/71296829/19334734)
     *
     */
    fun visible(): ClassField {
        return visibleSet().visibleGet()
    }

    fun visibleGet(): ClassField {
        if (field != null && !field.trySetAccessible()) {
            field.isAccessible = true
        }
        if (methodGet != null && !methodGet.trySetAccessible()) {
            methodGet.isAccessible = true
        }
        return this
    }

    fun visibleSet(): ClassField {
        if (field != null && !field.trySetAccessible()) {
            field.isAccessible = true
        }
        if (methodSet != null && !methodSet.trySetAccessible()) {
            methodSet.isAccessible = true
        }
        return this
    }

    // endregion

}
