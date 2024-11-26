package live.lingting.framework.util

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.Target
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024-02-02 17:42
 */
@Suppress("UNCHECKED_CAST")
object AnnotationUtils {
    @JvmField
    val NULL: Annotation = object : Annotation {}

    private val CACHE: MutableMap<AnnotatedElement, MutableMap<Class<out Annotation>, Annotation>> = ConcurrentHashMap()

    /**
     * 不对主体使用缓存进行查询
     */
    private fun <A : Annotation, E : AnnotatedElement> find(element: E, aClass: Class<A>): A? {
        // 1. 自身
        val annotation = element.getAnnotation(aClass)
        if (annotation != null) {
            return annotation
        }

        // 2. 自身注解内使用的注解
        val annotations = element.declaredAnnotations
        for (aa in annotations) {
            val a = findAnnotation(aa, aClass)
            if (a != null) {
                return a
            }
        }
        return null
    }

    /**
     * 按照以下顺序寻找注解. 深度优先
     * 1. 自身
     * 2. 自身注解内使用的注解
     * 3. 自身父级上的注解(依次寻找)
     * 4. 自身所实现的接口类上的注解(依次寻找)
     */
    @JvmStatic
    fun <A : Annotation> findAnnotation(cls: Class<*>, aClass: Class<A>): A? {
        val absent = CACHE.computeIfAbsent(cls) { ConcurrentHashMap() }
            .computeIfAbsent(aClass) {
                // 1 & 2 - 处理自身时不针对主体进行缓存
                var a = find(cls, aClass)
                if (a != null) {
                    return@computeIfAbsent a
                }

                // 3. 自身父级上的注解(依次寻找)
                val superclass = cls.superclass
                if (superclass != null) {
                    a = findAnnotation(superclass, aClass)
                    if (a != null) {
                        return@computeIfAbsent a
                    }
                }

                // 4. 自身所实现的接口类上的注解(依次寻找)
                for (ai in cls.interfaces) {
                    a = findAnnotation(ai, aClass)
                    if (a != null) {
                        return@computeIfAbsent a
                    }
                }
                NULL
            }
        return if (absent == NULL) null else absent as A
    }

    /**
     * 按照以下顺序寻找注解. 深度优先
     * 1. 自身
     * 2. 自身注解内使用的注解
     * 3. 自身所在的类(详见 {@see #findAnnotation(Class, Class)})
     */
    @JvmStatic
    fun <A : Annotation> findAnnotation(method: Method, aClass: Class<A>): A? {
        val absent = CACHE.computeIfAbsent(method) { ConcurrentHashMap() }.computeIfAbsent(aClass) {
            // 处理自身时不针对主体进行缓存
            var a = find(method, aClass)
            if (a != null) {
                return@computeIfAbsent a
            }
            val declaringClass = method.declaringClass
            a = findAnnotation(declaringClass, aClass)
            if (a != null) {
                return@computeIfAbsent a
            }
            NULL
        }
        return if (absent == NULL) null else absent as A
    }

    /**
     * 按照以下顺序寻找注解. 深度优先
     * 1. 自身
     * 2. 自身注解内使用的注解
     */
    @JvmStatic
    fun <A : Annotation, E : AnnotatedElement> findAnnotation(element: E, aClass: Class<A>): A? {
        val absent = CACHE.computeIfAbsent(element) {
            ConcurrentHashMap()
        }.computeIfAbsent(aClass) {
            find(element, aClass) ?: NULL
        }
        return if (absent == NULL) null else absent as A
    }

    @JvmStatic
    fun <A : Annotation?> findAnnotation(annotation: Annotation, aClass: Class<A>): A? {
        if (annotation is Documented || annotation is Retention || annotation is Target) {
            return null
        }
        val k = annotation.annotationClass
        return k.java.getAnnotation<A>(aClass)
    }
}

