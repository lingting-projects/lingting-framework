package live.lingting.framework.util

import live.lingting.framework.reflect.ClassField
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Collectors

/**
 * @author lingting 2021/2/25 21:17
 */
class ClassUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val EMPTY_CLASS_ARRAY: Array<Class<*>> = arrayOfNulls(0)


        val CACHE_CLASS_PRESENT: MutableMap<String, MutableMap<ClassLoader, Boolean>> = ConcurrentHashMap(8)

        val CACHE_FIELDS: MutableMap<Class<*>, Array<Field>> = ConcurrentHashMap(16)

        val CACHE_METHODS: MutableMap<Class<*>?, Array<Method?>> = ConcurrentHashMap(16)

        val CACHE_CLASS_FIELDS: MutableMap<Class<*>, Array<ClassField>> = ConcurrentHashMap(16)

        val CACHE_TYPE_ARGUMENTS: MutableMap<Class<*>, Array<Type?>> = ConcurrentHashMap()

        val CACHE_CONSTRUCTOR: MutableMap<Class<*>, Array<Constructor<*>>> = ConcurrentHashMap()

        /**
         * 获取指定类的泛型
         */
        fun typeArguments(cls: Class<*>): Array<Type?> {
            return CACHE_TYPE_ARGUMENTS.computeIfAbsent(cls) { k: Class<*>? ->
                val superclass = cls.genericSuperclass as? ParameterizedType ?: return@computeIfAbsent arrayOfNulls<Type>(0)
                superclass.actualTypeArguments
            }
        }

        fun classArguments(cls: Class<*>): List<Class<*>> {
            val types = typeArguments(cls)
            val list: MutableList<Class<*>> = ArrayList()

            for (type in types) {
                if (type is Class<*>) {
                    list.add(type)
                }
            }

            return list
        }

        /**
         * 判断class是否可以被加载, 使用系统类加载器和当前工具类加载器
         *
         * @param className 类名
         * @return true
         */

        fun isPresent(className: String): Boolean {
            return isPresent(className, ClassUtils::class.java.getClassLoader(), ClassLoader.getSystemClassLoader())
        }

        /**
         * 确定class是否可以被加载
         *
         * @param className    完整类名
         * @param classLoaders 类加载
         */

        fun isPresent(className: String, vararg classLoaders: ClassLoader): Boolean {
            val loaders: Collection<ClassLoader> = Arrays.stream<ClassLoader>(classLoaders)
                .filter { obj: ClassLoader? -> Objects.nonNull(obj) }
                .collect(Collectors.toCollection<ClassLoader, LinkedHashSet<ClassLoader>>(Supplier<LinkedHashSet<ClassLoader>> { LinkedHashSet() }))
            require(!CollectionUtils.isEmpty(loaders)) { "ClassLoaders can not be empty!" }
            val absent = CACHE_CLASS_PRESENT.computeIfAbsent(
                className
            ) { k: String? -> ConcurrentHashMap(loaders.size) }

            for (loader in loaders) {
                val flag = absent.computeIfAbsent(loader) { k: ClassLoader? ->
                    try {
                        Class.forName(className, true, loader)
                        return@computeIfAbsent true
                    } catch (ignored: Exception) {
                        //
                    }
                    false
                }
                if (java.lang.Boolean.TRUE == flag) {
                    return true
                }
            }

            return false
        }


        fun <T> scan(basePack: String): Set<Class<T>> {
            return scan(basePack, null)
        }


        fun <T> scan(basePack: String, cls: Class<*>?): Set<Class<T>> {
            return scan(basePack, { tClass: Class<T>? -> cls == null || cls.isAssignableFrom(tClass) }, { s: String?, e: Exception? -> })
        }

        /**
         * 扫描指定包下, 所有继承指定类的class
         *
         * @param basePack 指定包 eg: live.lingting.framework.item
         * @param filter   过滤指定类
         * @param error    获取类时发生异常处理
         * @return java.util.Set<java.lang.Class></java.lang.Class> < T>>
         */

        fun <T> scan(
            basePack: String, filter: Predicate<Class<T>?>,
            error: BiConsumer<String?, Exception?>
        ): Set<Class<T>> {
            val scanName: String = basePack.replace(".", "/")

            val collection: Collection<ResourceUtils.Resource> = ResourceUtils.scan(scanName,
                Predicate<ResourceUtils.Resource?> { resource: ResourceUtils.Resource? -> !resource!!.isDirectory && resource.getName().endsWith(".class") })

            val classes: MutableSet<Class<T>> = HashSet()
            for (resource in collection) {
                val last: String = StringUtils.substringAfterLast(resource.getPath(), scanName)
                val classPath: String = StringUtils.substringBeforeLast(scanName + last, ".")
                val className: String = classPath.replace("/", ".")

                try {
                    val aClass = Class.forName(className) as Class<T>

                    if (filter.test(aClass)) {
                        classes.add(aClass)
                    }
                } catch (e: Exception) {
                    error.accept(className, e)
                }
            }
            return classes
        }

        /**
         * 把指定对象的所有字段和对应的值组成Map
         *
         * @param o 需要转化的对象
         * @return java.util.Map<java.lang.String></java.lang.String>, java.lang.Object>
         */
        fun toMap(o: Any?): Map<String, Any?> {
            return toMap(o, { field: Field? -> true }, { obj: Field -> obj.name }, { field: Field?, v: Any? -> v })
        }

        /**
         * 把指定对象的所有字段和对应的值组成Map
         *
         * @param o      需要转化的对象
         * @param filter 过滤不存入Map的字段, 返回false表示不存入Map
         * @param toKey  设置存入Map的key
         * @param toVal  自定义指定字段值的存入Map的数据
         * @return java.util.Map<java.lang.String></java.lang.String>, java.lang.Object>
         */
        fun <T> toMap(
            o: Any?, filter: Predicate<Field?>, toKey: Function<Field, String>,
            toVal: BiFunction<Field?, Any?, T>
        ): Map<String, T> {
            if (o == null) {
                return emptyMap<String, T>()
            }
            val map = HashMap<String, T>()
            for (field in fields(o.javaClass)) {
                if (filter.test(field)) {
                    var `val`: Any? = null

                    try {
                        `val` = field[o]
                    } catch (e: IllegalAccessException) {
                        //
                    }

                    map.put(toKey.apply(field), toVal.apply(field, `val`))
                }
            }
            return map
        }

        /**
         * 获取所有字段, 不改变可读性
         *
         * @param cls 指定类
         * @return java.lang.reflect.Field[]
         */
        fun fields(cls: Class<*>): Array<Field> {
            return CACHE_FIELDS.computeIfAbsent(cls) { k: Class<*>? ->
                var k = k
                val fields: MutableList<Field> = ArrayList()
                while (k != null && !k.isAssignableFrom(Any::class.java)) {
                    fields.addAll(Arrays.asList(*k.declaredFields))
                    k = k.superclass
                }
                fields.toTypedArray<Field>()
            }
        }

        fun methods(cls: Class<*>?): Array<Method?> {
            return CACHE_METHODS.computeIfAbsent(cls) { k: Class<*>? ->
                var k = k
                val methods: MutableList<Method> = ArrayList()
                while (k != null && !k.isAssignableFrom(Any::class.java)) {
                    methods.addAll(Arrays.asList(*k.declaredMethods))
                    k = k.superclass
                }
                methods.toTypedArray<Method>()
            }
        }

        fun method(cls: Class<*>?, name: String): Method? {
            return method(cls, name, *EMPTY_CLASS_ARRAY)
        }

        fun method(cls: Class<*>?, name: String, vararg types: Class<*>?): Method? {
            return Arrays.stream<Method?>(methods(cls)).filter { method: Method? ->
                if (method!!.name != name) {
                    return@filter false
                }
                val len = types?.size ?: 0

                // 参数数量不一致
                if (len != method.parameterCount) {
                    return@filter false
                }
                // 无参
                if (len == 0) {
                    return@filter true
                }
                types.contentEquals(method.parameterTypes)
            }.findFirst().orElse(null)
        }

        /**
         * 扫描所有字段以及对应字段的值
         *
         *
         * 优先取指定字段的 get 方法, 仅会获取其中的无参方法
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
         * @return live.lingting.framework.domain.ClassField 可用于获取字段值的数组
         */
        fun classFields(cls: Class<*>): Array<ClassField> {
            return CACHE_CLASS_FIELDS.computeIfAbsent(cls) { k: Class<*>? ->
                var k = k
                val methods = methods(cls)

                val fields: MutableList<ClassField> = ArrayList()
                while (k != null && !k.isAssignableFrom(Any::class.java)) {
                    for (field in k.declaredFields) {
                        val fieldName: String = StringUtils.firstUpper(field.name)
                        // 尝试获取get方法
                        val getMethodName = "get$fieldName"

                        var optionalGet = Arrays.stream(methods)
                            .filter { method: Method? -> method!!.name == getMethodName && method.parameterCount == 0 }
                            .findFirst()

                        // get 不存在则尝试获取 is 方法
                        if (optionalGet.isEmpty) {
                            val isMethodName = "is$fieldName"
                            optionalGet = Arrays.stream(methods)
                                .filter { method: Method? -> method!!.name == isMethodName && method.parameterCount == 0 }
                                .findFirst()
                        }

                        // 尝试获取set方法
                        val setMethodName = "set$fieldName"
                        val optionalSet = Arrays.stream(methods)
                            .filter { method: Method? -> method!!.name.equals(setMethodName, ignoreCase = true) }
                            .findFirst()

                        fields.add(ClassField(field, optionalGet.orElse(null), optionalSet.orElse(null)))
                    }
                    k = k.superclass
                }
                fields.toTypedArray<ClassField>()
            }
        }

        /**
         * 获取指定类中的指定字段名的字段
         *
         * @param fieldName 字段名
         * @param cls       指定类
         * @return live.lingting.framework.domain.ClassField 字段
         */
        fun classField(fieldName: String, cls: Class<*>): ClassField? {
            for (field in classFields(cls)) {
                if (field.filedName == fieldName) {
                    return field
                }
            }
            return null
        }


        fun loadClass(className: String, classLoader: ClassLoader = ClassUtils::class.java.getClassLoader()): Class<*> {
            return loadClass(
                className, classLoader, ClassLoader.getSystemClassLoader(), ClassUtils::class.java.getClassLoader(),
                Thread.currentThread().contextClassLoader
            )
        }


        fun loadClass(className: String, vararg classLoaders: ClassLoader?): Class<*> {
            for (loader in classLoaders) {
                if (loader != null) {
                    try {
                        return loader.loadClass(className)
                    } catch (e: ClassNotFoundException) {
                        //
                    }
                }
            }
            throw ClassNotFoundException("$className not found")
        }

        /**
         * 设置可访问对象的可访问权限为 true
         *
         * @param object 可访问的对象
         * @param <T>    类型
         * @return 返回设置后的对象
        </T> */
        fun <T : AccessibleObject?> setAccessible(`object`: T): T {
            if (!`object`!!.trySetAccessible()) {
                `object`.isAccessible = true
            }
            return `object`
        }

        /**
         * 方法名转字段名
         *
         * @param methodName 方法名
         * @return java.lang.String 字段名
         */
        fun toFiledName(methodName: String): String {
            var methodName = methodName
            if (methodName.startsWith("is")) {
                methodName = methodName.substring(2)
            } else if (methodName.startsWith("get") || methodName.startsWith("set")) {
                methodName = methodName.substring(3)
            }

            if (methodName.length == 1 || (methodName.length > 1 && !Character.isUpperCase(methodName[1]))) {
                methodName = methodName.substring(0, 1).lowercase() + methodName.substring(1)
            }

            return methodName
        }

        fun <T> constructors(cls: Class<T>): Array<Constructor<T>> {
            return CACHE_CONSTRUCTOR.computeIfAbsent(cls) { obj: Class<*> -> obj.constructors } as Array<Constructor<T>>
        }
    }
}
