package live.lingting.framework.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2024-02-02 17:42
 */
@SuppressWarnings("unchecked")
public final class AnnotationUtils {

	public static final Annotation NULL = () -> null;

	private static final Map<AnnotatedElement, Map<Class<? extends Annotation>, Annotation>> CACHE = new ConcurrentHashMap<>();

	private static final Map<Class<?>, Map<Class<? extends Annotation>, Annotation>> CACHE_CLS = new ConcurrentHashMap<>();

	private AnnotationUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	/**
	 * 按照以下顺序寻找注解. 深度优先
	 * <p>
	 * 1. 自身
	 * </p>
	 * <p>
	 * 2. 自身注解内使用的注解
	 * </p>
	 * <p>
	 * 3. 自身父级上的注解(依次寻找)
	 * </p>
	 * <p>
	 * 4. 自身所实现的接口类上的注解(依次寻找)
	 * </p>
	 */
	public static <A extends Annotation> A findAnnotation(Class<?> cls, Class<A> aClass) {
		A absent = (A) CACHE_CLS.computeIfAbsent(cls, k -> new ConcurrentHashMap<>()).computeIfAbsent(aClass, k -> {
			// 1 & 2
			A a = findAnnotation((AnnotatedElement) cls, aClass);
			if (a != null) {
				return a;
			}

			// 3. 自身父级上的注解(依次寻找)
			Class<?> superclass = cls.getSuperclass();
			if (superclass != null) {
				a = findAnnotation(superclass, aClass);
				if (a != null) {
					return a;
				}
			}

			// 4. 自身所实现的接口类上的注解(依次寻找)
			for (Class<?> ai : cls.getInterfaces()) {
				a = findAnnotation(ai, aClass);
				if (a != null) {
					return a;
				}
			}

			return NULL;
		});
		return Objects.equals(absent, NULL) ? null : absent;
	}

	/**
	 * 按照以下顺序寻找注解. 深度优先
	 * <p>
	 * 1. 自身
	 * </p>
	 * <p>
	 * 2. 自身注解内使用的注解
	 * </p>
	 */
	public static <A extends Annotation> A findAnnotation(AnnotatedElement element, Class<A> aClass) {
		A absent = (A) CACHE.computeIfAbsent(element, k -> new ConcurrentHashMap<>()).computeIfAbsent(aClass, k -> {
			// 1. 自身
			A annotation = element.getAnnotation(aClass);
			if (annotation != null) {
				return annotation;
			}
			// 2. 自身注解内使用的注解
			Annotation[] annotations = element.getDeclaredAnnotations();
			for (Annotation aa : annotations) {
				A a = findAnnotation(aa, aClass);
				if (a != null) {
					return a;
				}
			}
			return NULL;
		});
		return Objects.equals(absent, NULL) ? null : absent;
	}

	public static <A extends Annotation> A findAnnotation(Annotation annotation, Class<A> aClass) {
		if (annotation instanceof Documented || annotation instanceof Retention || annotation instanceof Target) {
			return null;
		}
		return annotation.annotationType().getAnnotation(aClass);
	}

}
