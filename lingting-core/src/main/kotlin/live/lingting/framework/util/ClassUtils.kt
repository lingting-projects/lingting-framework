package live.lingting.framework.util

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import live.lingting.framework.reflect.ClassField

/**
 * @author lingting 2021/2/25 21:17
 */
@Suppress("UNCHECKED_CAST")
object ClassUtils {
    @JvmField
    val EMPTY_CLASS_ARRAY: Array<Class<*>> = arrayOf()

    @JvmField
    val CACHE_CLASS_PRESENT: MutableMap<String, MutableMap<ClassLoader, Boolean>> = ConcurrentHashMap(8)

    @JvmField
    val CACHE_FIELDS: MutableMap<Class<*>, Array<Field>> = ConcurrentHashMap(16)

    @JvmField
    val CACHE_METHODS: MutableMap<Class<*>, Array<Method>> = ConcurrentHashMap(16)

    @JvmField
    val CACHE_CLASS_FIELDS: MutableMap<Class<*>, Array<ClassField>> = ConcurrentHashMap(16)

    @JvmField
    val CACHE_TYPE_ARGUMENTS: MutableMap<Class<*>, Array<Type>> = ConcurrentHashMap()

    @JvmField
    val CACHE_CONSTRUCTOR: MutableMap<Class<*>, Array<Constructor<*>>> = ConcurrentHashMap()

    /**
     * 获取指定类的泛型
     */
    @JvmStatic
    fun typeArguments(cls: Class<*>): Array<Type> {
        return CACHE_TYPE_ARGUMENTS.computeIfAbsent(cls) {
            val superclass = cls.genericSuperclass
            if (superclass is ParameterizedType) {
                superclass.actualTypeArguments
            } else {
                arrayOf()
            }
        }
    }

    @JvmStatic
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
     * @param className 类名
     * @return true
     */
    @JvmStatic
    fun exists(className: String): Boolean {
        val classLoader = ClassUtils::class.java.classLoader
        val systemClassLoader = ClassLoader.getSystemClassLoader()
        return exists(className, classLoader, systemClassLoader)
    }

    /**
     * 确定class是否可以被加载
     * @param className    完整类名
     * @param classLoaders 类加载
     */
    @JvmStatic
    fun exists(className: String, vararg classLoaders: ClassLoader?): Boolean {
        val loaders = classLoaders.filterNotNull().toSet()
        require(!loaders.isNullOrEmpty()) { "ClassLoaders can not be empty!" }
        val absent = CACHE_CLASS_PRESENT.computeIfAbsent(
            className
        ) { k -> ConcurrentHashMap(loaders.size) }

        for (loader in loaders) {
            val flag = absent.computeIfAbsent(loader) {
                try {
                    Class.forName(className, true, loader)
                    true
                } catch (_: Exception) {
                    //
                    false
                }
            }
            if (java.lang.Boolean.TRUE == flag) {
                return true
            }
        }

        return false
    }

    @JvmStatic
    fun <T> scan(basePack: String): Set<Class<T>> {
        return scan(basePack, null)
    }

    @JvmStatic
    fun <T> scan(basePack: String, cls: Class<*>?): Set<Class<T>> {
        return scan(basePack, { tClass -> cls == null || cls.isAssignableFrom(tClass) }, { s, e -> })
    }

    /**
     * 扫描指定包下, 所有继承指定类的class
     * @param basePack 指定包 eg: live.lingting.framework.item
     * @param filter   过滤指定类
     * @param error    获取类时发生异常处理
     * @return java.util.Set<java.lang.Class></java.lang.Class> < T>>
     */

    @JvmStatic
    fun <T> scan(
        basePack: String, filter: Predicate<Class<T>>,
        error: BiConsumer<String, Exception>
    ): Set<Class<T>> {
        val scanName: String = basePack.replace(".", "/")

        val collection = ResourceUtils.scan(scanName) { !it.isDirectory && it.name.endsWith(".class") }

        val classes: MutableSet<Class<T>> = HashSet()
        for (resource in collection) {
            val last: String = StringUtils.substringAfterLast(resource.path, scanName)
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
     * @param o 需要转化的对象
     * @return java.util.Map<java.lang.String></java.lang.String>, java.lang.Object>
     */
    @JvmStatic
    fun toMap(o: Any?): Map<String, Any?> {
        return toMap(o, { true }, { it.name }, { _, v -> v })
    }

    /**
     * 把指定对象的所有字段和对应的值组成Map
     * @param o      需要转化的对象
     * @param filter 过滤不存入Map的字段, 返回false表示不存入Map
     * @param toKey  设置存入Map的key
     * @param toVal  自定义指定字段值的存入Map的数据
     * @return java.util.Map<java.lang.String></java.lang.String>, java.lang.Object>
     */
    @JvmStatic
    fun <T> toMap(
        o: Any?, filter: Predicate<Field>, toKey: Function<Field, String>,
        toVal: BiFunction<Field, Any?, T>
    ): Map<String, T> {
        if (o == null) {
            return emptyMap<String, T>()
        }
        val map = HashMap<String, T>()
        for (field in fields(o.javaClass)) {
            if (filter.test(field)) {
                var value: Any? = null

                try {
                    value = field[o]
                } catch (e: IllegalAccessException) {
                    //
                }

                map.put(toKey.apply(field), toVal.apply(field, value))
            }
        }
        return map
    }

    /**
     * 获取所有字段, 不改变可读性
     * @param cls 指定类
     * @return java.lang.reflect.Field[]
     */
    @JvmStatic
    fun fields(cls: Class<*>): Array<Field> {
        return CACHE_FIELDS.computeIfAbsent(cls) {
            var k: Class<*>? = cls
            val fields: MutableList<Field> = ArrayList()
            while (k != null && !k.isAssignableFrom(Any::class.java)) {
                fields.addAll(k.declaredFields)
                k = k.superclass
            }
            fields.toTypedArray<Field>()
        }
    }

    @JvmStatic
    fun methods(cls: Class<*>): Array<Method> {
        return CACHE_METHODS.computeIfAbsent(cls) {
            var k: Class<*>? = cls
            val methods: MutableList<Method> = ArrayList()
            while (k != null && !k.isAssignableFrom(Any::class.java)) {
                methods.addAll(k.declaredMethods)
                k = k.superclass
            }
            methods.toTypedArray<Method>()
        }
    }

    @JvmStatic
    fun method(cls: Class<*>, name: String): Method? {
        return method(cls, name, *EMPTY_CLASS_ARRAY)
    }

    @JvmStatic
    fun method(cls: Class<*>, name: String, vararg types: Class<*>): Method? {
        return methods(cls).find {
            if (it.name != name) {
                return@find false
            }
            val len = types.size

            // 参数数量不一致
            if (len != it.parameterCount) {
                return@find false
            }
            // 无参
            if (len == 0) {
                return@find true
            }
            types.contentEquals(it.parameterTypes)
        }
    }

    /**
     * 扫描所有字段以及对应字段的值
     * 优先取指定字段的 get 方法, 仅会获取其中的无参方法
     * 如果是 boolean 类型, 尝试取 is 方法
     * 否则直接取字段 - 不会尝试修改可读性, 如果可读性有问题, 请主动get 然后修改
     * @return live.lingting.framework.domain.ClassField 可用于获取字段值的数组
     */
    @JvmStatic
    fun classFields(cls: Class<*>): Array<ClassField> {
        return CACHE_CLASS_FIELDS.computeIfAbsent(cls) {
            var k: Class<*>? = cls
            val methods = methods(cls)

            val fields: MutableList<ClassField> = ArrayList()
            while (k != null && !k.isAssignableFrom(Any::class.java)) {
                for (field in k.declaredFields) {
                    val upper: String = StringUtils.firstUpper(field.name)
                    // 尝试获取get方法
                    val getMethodName = "get$upper"

                    var findGet = methods.find { it.name == getMethodName && it.parameterCount == 0 }
                    // get 不存在则尝试获取 is 方法
                    if (findGet == null) {
                        val isMethodName = "is$upper"
                        findGet = methods.find { it.name == isMethodName && it.parameterCount == 0 }
                    }

                    // 尝试获取set方法
                    val setMethodName = "set$upper"
                    var findSet = methods.find { it.name == setMethodName && it.parameterCount == 1 }

                    fields.add(ClassField(field, findGet, findSet))
                }
                k = k.superclass
            }
            fields.toTypedArray<ClassField>()
        }
    }

    /**
     * 获取指定类中的指定字段名的字段
     * @param name 字段名
     * @param cls       指定类
     * @return live.lingting.framework.domain.ClassField 字段
     */
    @JvmStatic
    fun classField(cls: Class<*>, name: String): ClassField? {
        return classFields(cls).find { it.name == name }
    }

    @JvmStatic
    fun loadClass(className: String): Class<*> {
        val loader = ClassUtils::class.java.getClassLoader()
        return loadClass(className, loader)
    }

    @JvmStatic
    fun loadClass(className: String, classLoader: ClassLoader): Class<*> {
        val systemClassLoader = ClassLoader.getSystemClassLoader()
        val classLoaders = ClassUtils::class.java.getClassLoader()
        val contextClassLoader = Thread.currentThread().contextClassLoader
        return loadClass(
            className,
            classLoader, systemClassLoader, classLoaders, contextClassLoader
        )
    }

    @JvmStatic
    fun loadClass(className: String, vararg classLoaders: ClassLoader?): Class<*> {
        for (loader in classLoaders) {
            if (loader == null) {
                continue
            }

            try {
                return loader.loadClass(className)
            } catch (_: ClassNotFoundException) {
                //
            }
        }
        throw ClassNotFoundException("$className not found")
    }

    /**
     * 设置可访问对象的可访问权限为 true
     * @param object 可访问的对象
     * @param <T>    类型
     * @return 返回设置后的对象
    </T> */
    @JvmStatic
    fun <T : AccessibleObject> setAccessible(`object`: T): T {
        if (!`object`.trySetAccessible()) {
            `object`.isAccessible = true
        }
        return `object`
    }

    /**
     * 方法名转字段名
     * @param methodName 方法名
     * @return java.lang.String 字段名
     */
    @JvmStatic
    fun toFiledName(methodName: String): String {
        val trim = when {
            methodName.startsWith("is") -> StringUtils.firstLower(methodName.substring(2))
            methodName.startsWith("get") || methodName.startsWith("set") -> StringUtils.firstLower(methodName.substring(3))
            else -> methodName
        }
        return StringUtils.firstLower(trim)
    }

    @JvmStatic
    fun <T> constructors(cls: Class<T>): Array<Constructor<T>> {
        return CACHE_CONSTRUCTOR.computeIfAbsent(cls) { it.constructors } as Array<Constructor<T>>
    }

}
