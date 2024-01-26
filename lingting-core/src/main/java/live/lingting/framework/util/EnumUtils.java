package live.lingting.framework.util;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import live.lingting.framework.domain.ClassField;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2022/12/20 14:52
 */
@UtilityClass
public class EnumUtils {

	public static final String METHOD_GET_VALUE = "getValue";

	public static final String CLS_MYBATIS_PLUS_IENUM = "com.baomidou.mybatisplus.annotation.IEnum";

	public static final String CLS_JACKSON_JSON_VALUE = "com.fasterxml.jackson.annotation.JsonValue";

	static final Map<Class<?>, ClassField> CACHE = new ConcurrentHashMap<>();

	public static ClassField getByIEnum(Class<?> cls) {
		if (!ClassUtils.isPresent(CLS_MYBATIS_PLUS_IENUM, EnumUtils.class.getClassLoader())) {
			return null;
		}

		Method method = null;
		if (IEnum.class.isAssignableFrom(cls)) {
			method = ClassUtils.method(cls, METHOD_GET_VALUE);
		}

		if (method == null) {
			return null;
		}
		return new ClassField(null, method, null);
	}

	public static ClassField getByJsonValue(Class<?> cls) {
		if (!ClassUtils.isPresent(CLS_JACKSON_JSON_VALUE, EnumUtils.class.getClassLoader())) {
			return null;
		}

		Method method = null;
		Field field = getJsonValueField(cls);
		if (field != null) {
			// public 字段
			if (Modifier.isPublic(field.getModifiers())) {
				return new ClassField(field, null, null);
			}

			String name = "get" + StringUtils.firstUpper(field.getName());
			// 获取 get 方法
			method = ClassUtils.method(cls, name);
			if (method != null) {
				return new ClassField(null, method, null);
			}
		}

		method = getJsonValueMethod(cls);
		if (method != null) {
			return new ClassField(null, method, null);
		}
		return null;
	}

	public static Method getJsonValueMethod(Class<?> cls) {
		// 获取public的方法.
		Method[] methods = cls.getMethods();
		for (Method method : methods) {
			Annotation annotation = method.getAnnotation(JsonValue.class);
			// 存在注解且参数为空
			if (annotation != null && ArrayUtils.isEmpty(method.getParameters())) {
				return method;
			}
		}
		return null;
	}

	public static Field getJsonValueField(Class<?> cls) {
		Field[] fields = cls.getDeclaredFields();

		for (Field field : fields) {
			Annotation annotation = field.getAnnotation(JsonValue.class);
			if (annotation != null) {
				return field;
			}
		}
		return null;
	}

	public static ClassField getByName(Class<?> cls) {
		Method method = ClassUtils.method(cls, "name");
		if (method != null) {
			return new ClassField(null, method, null);
		}
		return null;
	}

	public static ClassField getCf(Class<?> cls) {
		return CACHE.computeIfAbsent(cls, k -> {
			ClassField cf = getByIEnum(cls);

			if (cf == null) {
				cf = getByJsonValue(cls);
			}

			if (cf == null) {
				cf = getByName(cls);
			}

			return cf;
		});
	}

	public static <E extends Enum<E>> Object getValue(Enum<E> e) {
		if (e == null) {
			return null;
		}
		ClassField cf = getCf(e.getClass());
		try {
			return cf.get(e);
		}
		catch (Exception ex) {
			return null;
		}
	}

}
