package live.lingting.framework.util;

import live.lingting.framework.reflect.ClassField;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author lingting 2021/2/25 21:17
 */
@SuppressWarnings({"java:S3011", "unchecked"})
public final class ClassUtils {

	static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

	static final Map<String, Map<ClassLoader, Boolean>> CACHE_CLASS_PRESENT = new ConcurrentHashMap<>(8);

	static final Map<Class<?>, Field[]> CACHE_FIELDS = new ConcurrentHashMap<>(16);

	static final Map<Class<?>, Method[]> CACHE_METHODS = new ConcurrentHashMap<>(16);

	static final Map<Class<?>, ClassField[]> CACHE_CLASS_FIELDS = new ConcurrentHashMap<>(16);

	static final Map<Class<?>, Type[]> CACHE_TYPE_ARGUMENTS = new ConcurrentHashMap<>();

	static final Map<Class<?>, Constructor<?>[]> CACHE_CONSTRUCTOR = new ConcurrentHashMap<>();

	private ClassUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	/**
	 * 获取指定类的泛型
	 */
	public static Type[] typeArguments(Class<?> cls) {
		return CACHE_TYPE_ARGUMENTS.computeIfAbsent(cls, k -> {
			Type superclass = cls.getGenericSuperclass();

			if (!(superclass instanceof ParameterizedType)) {
				return new Type[0];
			}

			return ((ParameterizedType) superclass).getActualTypeArguments();
		});
	}

	public static List<Class<?>> classArguments(Class<?> cls) {
		Type[] types = typeArguments(cls);
		List<Class<?>> list = new ArrayList<>();

		for (Type type : types) {
			if (type instanceof Class<?>) {
				list.add((Class<?>) type);
			}
		}

		return list;
	}

	/**
	 * 判断class是否可以被加载, 使用系统类加载器和当前工具类加载器
	 *
	 * @param className 类名
	 * @return true
	 */
	public static boolean isPresent(String className) {
		return isPresent(className, ClassUtils.class.getClassLoader(), ClassLoader.getSystemClassLoader());
	}

	/**
	 * 确定class是否可以被加载
	 *
	 * @param className    完整类名
	 * @param classLoaders 类加载
	 */
	public static boolean isPresent(String className, ClassLoader... classLoaders) {
		Collection<ClassLoader> loaders = Arrays.stream(classLoaders)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		if (CollectionUtils.isEmpty(loaders)) {
			throw new IllegalArgumentException("ClassLoaders can not be empty!");
		}
		Map<ClassLoader, Boolean> absent = CACHE_CLASS_PRESENT.computeIfAbsent(className,
			k -> new ConcurrentHashMap<>(loaders.size()));

		for (ClassLoader loader : loaders) {
			Boolean flag = absent.computeIfAbsent(loader, k -> {
				try {
					Class.forName(className, true, loader);
					return true;
				}
				catch (Exception ignored) {
					//
				}
				return false;
			});
			if (Boolean.TRUE.equals(flag)) {
				return true;
			}
		}

		return false;
	}

	public static <T> Set<Class<T>> scan(String basePack) throws IOException {
		return scan(basePack, null);
	}

	public static <T> Set<Class<T>> scan(String basePack, Class<?> cls) throws IOException {
		return scan(basePack, tClass -> cls == null || cls.isAssignableFrom(tClass), (s, e) -> {
		});
	}

	/**
	 * 扫描指定包下, 所有继承指定类的class
	 *
	 * @param basePack 指定包 eg: live.lingting.framework.item
	 * @param filter   过滤指定类
	 * @param error    获取类时发生异常处理
	 * @return java.util.Set<java.lang.Class < T>>
	 */
	@SuppressWarnings("java:S3776")
	public static <T> Set<Class<T>> scan(String basePack, Predicate<Class<T>> filter,
										 BiConsumer<String, Exception> error) throws IOException {
		String scanName = basePack.replace(".", "/");

		Collection<ResourceUtils.Resource> collection = ResourceUtils.scan(scanName,
			resource -> !resource.isDirectory() && resource.getName().endsWith(".class"));

		Set<Class<T>> classes = new HashSet<>();
		for (ResourceUtils.Resource resource : collection) {
			String last = StringUtils.substringAfterLast(resource.getPath(), scanName);
			String classPath = StringUtils.substringBeforeLast(scanName + last, ".");
			String className = classPath.replace("/", ".");

			try {
				Class<T> aClass = (Class<T>) Class.forName(className);

				if (filter.test(aClass)) {
					classes.add(aClass);
				}
			}
			catch (Exception e) {
				error.accept(className, e);
			}
		}
		return classes;
	}

	/**
	 * 把指定对象的所有字段和对应的值组成Map
	 *
	 * @param o 需要转化的对象
	 * @return java.util.Map<java.lang.String, java.lang.Object>
	 */
	public static Map<String, Object> toMap(Object o) {
		return toMap(o, field -> true, Field::getName, (field, v) -> v);
	}

	/**
	 * 把指定对象的所有字段和对应的值组成Map
	 *
	 * @param o      需要转化的对象
	 * @param filter 过滤不存入Map的字段, 返回false表示不存入Map
	 * @param toKey  设置存入Map的key
	 * @param toVal  自定义指定字段值的存入Map的数据
	 * @return java.util.Map<java.lang.String, java.lang.Object>
	 */
	public static <T> Map<String, T> toMap(Object o, Predicate<Field> filter, Function<Field, String> toKey,
										   BiFunction<Field, Object, T> toVal) {
		if (o == null) {
			return Collections.emptyMap();
		}
		HashMap<String, T> map = new HashMap<>();
		for (Field field : fields(o.getClass())) {
			if (filter.test(field)) {
				Object val = null;

				try {
					val = field.get(o);
				}
				catch (IllegalAccessException e) {
					//
				}

				map.put(toKey.apply(field), toVal.apply(field, val));
			}
		}
		return map;
	}

	/**
	 * 获取所有字段, 不改变可读性
	 *
	 * @param cls 指定类
	 * @return java.lang.reflect.Field[]
	 */
	public static Field[] fields(Class<?> cls) {
		return CACHE_FIELDS.computeIfAbsent(cls, k -> {
			List<Field> fields = new ArrayList<>();
			while (k != null && !k.isAssignableFrom(Object.class)) {
				fields.addAll(Arrays.asList(k.getDeclaredFields()));
				k = k.getSuperclass();
			}
			return fields.toArray(new Field[0]);
		});
	}

	public static Method[] methods(Class<?> cls) {
		return CACHE_METHODS.computeIfAbsent(cls, k -> {
			List<Method> methods = new ArrayList<>();
			while (k != null && !k.isAssignableFrom(Object.class)) {
				methods.addAll(Arrays.asList(k.getDeclaredMethods()));
				k = k.getSuperclass();
			}
			return methods.toArray(new Method[0]);
		});
	}

	public static Method method(Class<?> cls, String name) {
		return method(cls, name, EMPTY_CLASS_ARRAY);
	}

	public static Method method(Class<?> cls, String name, Class<?>... types) {
		return Arrays.stream(methods(cls)).filter(method -> {
			if (!Objects.equals(method.getName(), name)) {
				return false;
			}

			int len = types == null ? 0 : types.length;

			// 参数数量不一致
			if (len != method.getParameterCount()) {
				return false;
			}
			// 无参
			if (len == 0) {
				return true;
			}

			// 参数类型是否一致
			return Arrays.equals(types, method.getParameterTypes());
		}).findFirst().orElse(null);
	}

	/**
	 * 扫描所有字段以及对应字段的值
	 * <p>
	 * 优先取指定字段的 get 方法, 仅会获取其中的无参方法
	 * </p>
	 * <p>
	 * 如果是 boolean 类型, 尝试取 is 方法
	 * </p>
	 * <p>
	 * 否则直接取字段 - 不会尝试修改可读性, 如果可读性有问题, 请主动get 然后修改
	 * </p>
	 *
	 * @return live.lingting.framework.domain.ClassField 可用于获取字段值的数组
	 */
	public static ClassField[] classFields(Class<?> cls) {
		return CACHE_CLASS_FIELDS.computeIfAbsent(cls, k -> {
			Method[] methods = methods(cls);

			List<ClassField> fields = new ArrayList<>();
			while (k != null && !k.isAssignableFrom(Object.class)) {
				for (Field field : k.getDeclaredFields()) {
					String fieldName = StringUtils.firstUpper(field.getName());
					// 尝试获取get方法
					String getMethodName = "get" + fieldName;

					Optional<Method> optionalGet = Arrays.stream(methods)
						.filter(method -> method.getName().equals(getMethodName) && method.getParameterCount() == 0)
						.findFirst();

					// get 不存在则尝试获取 is 方法
					if (optionalGet.isEmpty()) {
						String isMethodName = "is" + fieldName;
						optionalGet = Arrays.stream(methods)
							.filter(method -> method.getName().equals(isMethodName) && method.getParameterCount() == 0)
							.findFirst();
					}

					// 尝试获取set方法
					String setMethodName = "set" + fieldName;
					Optional<Method> optionalSet = Arrays.stream(methods)
						.filter(method -> method.getName().equalsIgnoreCase(setMethodName))
						.findFirst();

					fields.add(new ClassField(field, optionalGet.orElse(null), optionalSet.orElse(null)));
				}
				k = k.getSuperclass();
			}
			return fields.toArray(new ClassField[0]);
		});
	}

	/**
	 * 获取指定类中的指定字段名的字段
	 *
	 * @param fieldName 字段名
	 * @param cls       指定类
	 * @return live.lingting.framework.domain.ClassField 字段
	 */
	public static ClassField classField(String fieldName, Class<?> cls) {
		for (ClassField field : classFields(cls)) {
			if (field.getFiledName().equals(fieldName)) {
				return field;
			}
		}
		return null;
	}

	public static Class<?> loadClass(String className) throws ClassNotFoundException {
		return loadClass(className, ClassUtils.class.getClassLoader());
	}

	public static Class<?> loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
		return loadClass(className, classLoader, ClassLoader.getSystemClassLoader(), ClassUtils.class.getClassLoader(),
			Thread.currentThread().getContextClassLoader());
	}

	public static Class<?> loadClass(String className, ClassLoader... classLoaders) throws ClassNotFoundException {
		for (ClassLoader loader : classLoaders) {
			if (loader != null) {
				try {
					return loader.loadClass(className);
				}
				catch (ClassNotFoundException e) {
					//
				}
			}
		}
		throw new ClassNotFoundException(className + " not found");
	}

	/**
	 * 设置可访问对象的可访问权限为 true
	 *
	 * @param object 可访问的对象
	 * @param <T>    类型
	 * @return 返回设置后的对象
	 */
	public static <T extends AccessibleObject> T setAccessible(T object) {
		if (!object.trySetAccessible()) {
			object.setAccessible(true);
		}
		return object;
	}

	/**
	 * 方法名转字段名
	 *
	 * @param methodName 方法名
	 * @return java.lang.String 字段名
	 */
	public static String toFiledName(String methodName) {
		if (methodName.startsWith("is")) {
			methodName = methodName.substring(2);
		}
		else if (methodName.startsWith("get") || methodName.startsWith("set")) {
			methodName = methodName.substring(3);
		}

		if (methodName.length() == 1 || (methodName.length() > 1 && !Character.isUpperCase(methodName.charAt(1)))) {
			methodName = methodName.substring(0, 1).toLowerCase(Locale.ENGLISH) + methodName.substring(1);
		}

		return methodName;
	}

	@SuppressWarnings("unchecked")
	public static <T> Constructor<T>[] constructors(Class<T> cls) {
		return (Constructor<T>[]) CACHE_CONSTRUCTOR.computeIfAbsent(cls, Class::getConstructors);
	}

}
