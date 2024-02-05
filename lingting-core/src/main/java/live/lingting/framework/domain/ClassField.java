package live.lingting.framework.domain;

import live.lingting.framework.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 用于获取指定字段的值
 * <p>
 * 优先取指定字段的 get 方法
 * </p>
 * <p>
 * 如果是 boolean 类型, 尝试取 is 方法
 * </p>
 * <p>
 * 否则直接取字段 - 不会尝试修改可读性, 如果可读性有问题, 请主动get 然后修改
 * </p>
 *
 * @author lingting 2022/12/6 13:04
 */
@SuppressWarnings("java:S3011")
public record ClassField(Field field, Method methodGet, Method methodSet) {

	public String getFiledName() {
		return field.getName();
	}

	/**
	 * 是否拥有指定注解, 会同时对 字段 和 方法进行判断
	 * @param a 注解类型
	 * @return boolean true 表示拥有
	 */
	public <T extends Annotation> T getAnnotation(Class<T> a) {
		// 字段上找
		T annotation = getAnnotation(field, a);
		// 方法上找
		if (annotation == null) {
			annotation = getAnnotation(methodGet, a);
		}
		if (annotation == null) {
			annotation = getAnnotation(methodSet, a);
		}
		return annotation;
	}

	<T extends Annotation> T getAnnotation(AccessibleObject object, Class<T> a) {
		return object == null ? null : AnnotationUtils.findAnnotation(object, a);
	}

	/**
	 * 获取字段值, 仅支持无参方法
	 * @param obj 对象
	 * @return java.lang.Object 对象指定字段值
	 */
	public Object get(Object obj) throws IllegalAccessException, InvocationTargetException {
		if (methodGet != null) {
			return methodGet.invoke(obj);
		}
		return field.get(obj);
	}

	/**
	 * 设置字段值
	 * @param obj 对象
	 * @param args set方法参数, 如果无set方法, 则第一个参数会被作为值通过字段设置
	 */
	public void set(Object obj, Object... args) throws InvocationTargetException, IllegalAccessException {
		if (methodSet != null) {
			methodSet.invoke(obj, args);
			return;
		}
		field.set(obj, args[0]);
	}

	public Class<?> getValueType() {
		return field == null ? methodGet.getReturnType() : field.getType();
	}

	// region visible

	/**
	 * 是否能够获取值
	 */
	public boolean canGet(Object o) {
		if (methodGet != null) {
			return methodGet.canAccess(o);
		}
		if (field != null) {
			return field.canAccess(o);
		}
		return false;
	}

	/**
	 * 是否能够设置值
	 */
	public boolean canSet(Object o) {
		if (methodSet == null) {
			return false;
		}
		return methodSet.canAccess(o);
	}

	/**
	 * 将方法转化为可见的
	 * <p>
	 * 未声明可能会调用异常. <a href="https://stackoverflow.com/a/71296829/19334734">相关回答</a>
	 * </p>
	 */
	public ClassField visible() {
		return visibleSet().visibleGet();
	}

	public ClassField visibleGet() {
		if (field != null && !field.trySetAccessible()) {
			field.setAccessible(true);
		}
		if (methodGet != null && !methodGet.trySetAccessible()) {
			methodGet.setAccessible(true);
		}
		return this;
	}

	public ClassField visibleSet() {
		if (field != null && !field.trySetAccessible()) {
			field.setAccessible(true);
		}
		if (methodSet != null && !methodSet.trySetAccessible()) {
			methodSet.setAccessible(true);
		}
		return this;
	}

	// endregion

	// region get

	// endregion

	// region field

	public boolean hasField() {
		return field != null;
	}

	public boolean isFinalField() {
		return field != null && Modifier.isFinal(field.getModifiers());
	}

	// endregion

}
