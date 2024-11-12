package live.lingting.framework.util

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.Target
import java.lang.reflect.AnnotatedElement
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024-02-02 17:42
 */
class AnnotationUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val NULL: Annotation = Annotation { null }

        private val CACHE: MutableMap<AnnotatedElement, MutableMap<Class<out Annotation>, Annotation>> = ConcurrentHashMap()

        private val CACHE_CLS: MutableMap<Class<*>, MutableMap<Class<out Annotation>, Annotation>> = ConcurrentHashMap()

        /**
         * 按照以下顺序寻找注解. 深度优先
         *
         *
         * 1. 自身
         *
         *
         *
         * 2. 自身注解内使用的注解
         *
         *
         *
         * 3. 自身父级上的注解(依次寻找)
         *
         *
         *
         * 4. 自身所实现的接口类上的注解(依次寻找)
         *
         */
        fun <A : Annotation?> findAnnotation(cls: Class<*>, aClass: Class<A>): A? {
            val absent = CACHE_CLS.computeIfAbsent(cls) { k: Class<*>? -> ConcurrentHashMap() }.computeIfAbsent(aClass) { k: Class<out Annotation>? ->
                // 1 & 2
                var a = findAnnotation(cls as AnnotatedElement, aClass)
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
            } as A
            return if (absent == NULL) null else absent
        }

        /**
         * 按照以下顺序寻找注解. 深度优先
         *
         *
         * 1. 自身
         *
         *
         *
         * 2. 自身注解内使用的注解
         *
         */
        fun <A : Annotation?> findAnnotation(element: AnnotatedElement, aClass: Class<A>): A? {
            val absent = CACHE.computeIfAbsent(element) { k: AnnotatedElement? -> ConcurrentHashMap() }.computeIfAbsent(aClass) { k: Class<out Annotation>? ->
                // 1. 自身
                val annotation = element.getAnnotation(aClass)
                if (annotation != null) {
                    return@computeIfAbsent annotation
                }
                // 2. 自身注解内使用的注解
                val annotations = element.declaredAnnotations
                for (aa in annotations) {
                    val a = findAnnotation(aa, aClass)
                    if (a != null) {
                        return@computeIfAbsent a
                    }
                }
                NULL
            } as A
            return if (absent == NULL) null else absent
        }

        fun <A : Annotation?> findAnnotation(annotation: Annotation, aClass: Class<A>): A? {
            if (annotation is Documented || annotation is Retention || annotation is Target) {
                return null
            }
            return annotation.annotationType().getAnnotation<A>(aClass)
        }
    }
}
