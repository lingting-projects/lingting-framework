package live.lingting.framework.util

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import kotlin.reflect.KClass
import live.lingting.framework.reflect.ClassField
import live.lingting.framework.util.StringUtils.firstLower
import live.lingting.framework.util.StringUtils.firstUpper

/**
 * @author lingting 2021/2/25 21:17
 */
@Suppress("UNCHECKED_CAST")
object ClassUtils {

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

    @JvmField
    val FIELD_METHOD_START = setOf("is", "get", "set")

    /**
     * 获取指定类的泛型
     */
    @JvmStatic
    fun typeArguments(cls: Class<*>): Array<Type> {
        return CACHE_TYPE_ARGUMENTS.computeIfAbsent(cls) {
            val types = ArrayList<Type>()

            // 父类
            val superclass = cls.genericSuperclass
            if (superclass is ParameterizedType) {
                types.addAll(superclass.actualTypeArguments)
            }

            // 接口类
            val interfaces = cls.genericInterfaces
            for (inter in interfaces) {
                if (inter is ParameterizedType) {
                    types.addAll(inter.actualTypeArguments)
                }
            }

            types.toTypedArray()
        }
    }

    fun typeArguments(cls: KClass<*>) = typeArguments(cls.java)

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

    fun classArguments(cls: KClass<*>) = classArguments(cls.java)

    @JvmStatic
    fun classLoaders(vararg loaders: ClassLoader?): Set<ClassLoader> {
        val set = HashSet<ClassLoader>()
        set.addAll(loaders.filterNotNull())
        set.add(ClassUtils::class.java.classLoader)
        set.add(ClassLoader.getSystemClassLoader())
        set.add(Thread.currentThread().contextClassLoader)
        return set
    }

    /**
     * 判断class是否可以被加载, 使用系统类加载器和当前工具类加载器
     * @param className 类名
     * @return true
     */
    @JvmStatic
    fun exists(className: String): Boolean {
        val loaders = classLoaders(className::class.java.classLoader)
        return exists(className, loaders)
    }

    /**
     * 确定class是否可以被加载
     * @param className    完整类名
     * @param classLoaders 类加载
     */
    @JvmStatic
    fun exists(className: String, vararg classLoaders: ClassLoader?): Boolean {
        return exists(className, classLoaders(*classLoaders))
    }

    @JvmStatic
    fun exists(className: String, loaders: Set<ClassLoader>): Boolean {
        return try {
            loadClass(className, loaders)
            true
        } catch (_: Exception) {
            //
            false
        }

    }

    @JvmStatic
    fun loadClass(className: String): Class<*> {
        return loadClass(className, *emptyArray())
    }

    @JvmStatic
    fun loadClass(className: String, vararg classLoaders: ClassLoader?): Class<*> {
        return loadClass(className, classLoaders(*classLoaders, className::class.java.classLoader))
    }

    @JvmStatic
    fun loadClass(className: String, loaders: Set<ClassLoader>): Class<*> {
        if (Objects.equals(className, ClassUtils::class.java.name)) {
            return ClassUtils::class.java
        }
        require(loaders.isNotEmpty()) { "ClassLoaders can not be empty!" }

        var ex: Exception? = null
        for (loader in loaders) {
            try {
                return Class.forName(className, false, loader)
            } catch (e: Exception) {
                ex = e
            }
        }

        throw ex ?: ClassNotFoundException(className)
    }

    @JvmField
    val CLASS_SUFFIX = setOf(".java", ".class")

    @JvmStatic
    fun convertClassName(className: String): String {
        var c1 = className.replace("/", ".").replace("\\", ".")
        if (c1.startsWith(".")) {
            c1 = c1.substring(1)
        }

        CLASS_SUFFIX.forEach {
            if (c1.endsWith(it)) {
                c1 = c1.substring(0, c1.length - it.length)
            }
        }
        return c1
    }

    val SCAN_IGNORE_PREFIX = setOf("META-INFO", "/META-INFO", "META-INF", "/META-INF")

    val SCAN_IGNORE_NAME = setOf("module-info.class", "package-info.class", "module-info.java", "package-info.java")

    @JvmStatic
    @JvmOverloads
    fun <T : Any> scan(basePack: String, cls: Class<*>? = null): Set<Class<T>> {
        return scan<T>(basePack, Predicate { cls == null || cls.isAssignableFrom(it) }, classLoaders(cls?.classLoader))
    }

    fun <T : Any> scan(basePack: String, cls: KClass<T>?) = scan<T>(basePack, cls?.java)

    /**
     * 扫描指定包下, 所有继承指定类的class
     * @param basePack 指定包 eg: live.lingting.framework.item
     * @param filter   过滤指定类
     * @param error    获取类时发生异常处理
     * @return java.util.Set<java.lang.Class></java.lang.Class> < T>>
     */
    @JvmStatic
    @JvmOverloads
    fun <T> scan(
        basePack: String, filter: Predicate<Class<T>>,
        loaders: Set<ClassLoader> = classLoaders(),
        error: BiConsumer<String, Throwable> = BiConsumer { _, _ -> },
    ): Set<Class<T>> {
        val path = Resource.convertPath(basePack).replace(".", "/")

        val collection = ResourceUtils.scan(path) {
            !it.isDirectory && it.name.endsWith(".class")
                    && !SCAN_IGNORE_PREFIX.any { prefix -> it.path.startsWith(prefix) }
                    && !SCAN_IGNORE_NAME.contains(it.name)
        }

        val classes: MutableSet<Class<T>> = HashSet()
        for (resource in collection) {
            val last = resource.path
            val link = path + last
            val name = convertClassName(link)

            try {
                val aClass = loadClass(name, loaders) as Class<T>
                if (filter.test(aClass)) {
                    classes.add(aClass)
                }
            } catch (e: Throwable) {
                error.accept(name, e)
            }
        }
        return classes
    }

    fun <T : Any> scan(basePack: String, filter: Predicate<KClass<T>>, error: BiConsumer<String, Throwable>) = {
        scan<T>(basePack, Predicate<Class<T>> { filter.test(it.kotlin) }, error = error)
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

    fun fields(cls: KClass<*>) = fields(cls.java)

    @JvmStatic
    fun field(cls: Class<*>, name: String): Field? {
        return fields(cls).find { it.name == name }
    }

    fun field(cls: KClass<*>, name: String) = field(cls.java, name)

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

    fun methods(cls: KClass<*>) = methods(cls.java)

    @JvmStatic
    fun method(cls: Class<*>, name: String): Method? {
        return method(cls, name, *emptyArray())
    }

    fun method(cls: KClass<*>, name: String) = method(cls.java, name)

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

    fun method(cls: KClass<*>, name: String, vararg types: Class<*>) = method(cls.java, name, *types)

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
                    val upper: String = field.name.firstUpper()
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

    fun classFields(cls: KClass<*>) = classFields(cls.java)

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

    fun classField(cls: KClass<*>, name: String) = classField(cls.java, name)

    /**
     * 方法名转字段名
     * @param methodName 方法名
     * @return java.lang.String 字段名
     */
    @JvmStatic
    fun toFiledName(methodName: String): String {
        val trim = FIELD_METHOD_START.firstOrNull {
            methodName.startsWith(it)
        }?.let {
            methodName.substring(it.length)
        } ?: methodName

        return trim.firstLower()
    }

    @JvmStatic
    fun isSuper(cls: Class<*>?, superClassName: String): Boolean {
        if (cls == null) {
            return false
        }
        if (superClassName == Any::class.java.name) {
            return true
        }

        if (cls == Any::class.java) {
            return false
        }

        if (cls.name == superClassName) {
            return true
        }

        // 找父类
        if (isSuper(cls.superclass, superClassName)) {
            return true
        }

        // 接口
        return cls.interfaces.any { isSuper(it, superClassName) }
    }

    /**
     * cls 是否是 superClass 的子类
     */
    @JvmStatic
    fun isSuper(cls: Class<*>, superClass: Class<*>): Boolean {
        if (superClass == Any::class.java) {
            return true
        }
        if (cls == Any::class.java) {
            return false
        }
        return superClass.isAssignableFrom(cls)
    }

    fun isSuper(cls: KClass<*>, superClass: KClass<*>) = isSuper(cls.java, superClass.java)

    @JvmStatic
    fun <T> constructors(cls: Class<T>): Array<Constructor<T>> {
        return CACHE_CONSTRUCTOR.computeIfAbsent(cls) { it.constructors } as Array<Constructor<T>>
    }

    fun <T : Any> constructors(cls: KClass<T>) = constructors(cls.java)

    @JvmStatic
    inline val Class<*>.isPublic: Boolean get() = Modifier.isPublic(modifiers)

    @JvmStatic
    inline val Class<*>.isProtected: Boolean get() = Modifier.isProtected(modifiers)

    @JvmStatic
    inline val Class<*>.isPrivate: Boolean get() = Modifier.isPrivate(modifiers)

    @JvmStatic
    inline val Class<*>.isFinal: Boolean get() = Modifier.isFinal(modifiers)

    @JvmStatic
    inline val Class<*>.isStatic: Boolean get() = Modifier.isStatic(modifiers)

    @JvmStatic
    inline val Class<*>.isAbstract: Boolean get() = Modifier.isAbstract(modifiers)

}
