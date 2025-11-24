package live.lingting.framework.util

import live.lingting.framework.reflect.ClassField
import live.lingting.framework.util.ClassUtils.classLoaders
import live.lingting.framework.util.FieldUtils.isFinal
import live.lingting.framework.util.StringUtils.firstLower
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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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

    @JvmStatic
    fun classLoaders(vararg loaders: ClassLoader?): Set<ClassLoader> {
        val set = LinkedHashSet<ClassLoader>()
        set.addAll(loaders.filterNotNull())
        set.add(ClassUtils::class.java.classLoader)
        set.add(Thread.currentThread().contextClassLoader)
        set.add(ClassLoader.getSystemClassLoader())
        set.add(ClassLoader.getPlatformClassLoader())
        return set
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

    /**
     * 确定class是否可以被加载
     * @param className    完整类名
     * @param classLoaders 类加载
     */
    @JvmStatic
    @JvmOverloads
    fun exists(name: String, loaders: Set<ClassLoader> = classLoaders()): Boolean {
        return try {
            loadClass(name, loaders)
            true
        } catch (_: Exception) {
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

        var ex: Throwable? = null
        for (loader in loaders) {
            try {
                return loader.loadClass(className)
            } catch (e: Throwable) {
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
    fun <T> scan(basePack: String, cls: Class<T>? = null): Set<Class<T>> {
        return scan(basePack, Predicate { cls == null || isSuper(it, cls) }, classLoaders(cls?.classLoader))
    }

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
        basePack: String, filter: Predicate<Class<*>>,
        loaders: Set<ClassLoader> = classLoaders(),
        error: BiConsumer<String, Throwable> = BiConsumer { _, _ -> },
    ): Set<Class<T>> {
        val path = basePack.replace("\\", "/").replace(".", "/")

        val collection = ResourceUtils.scan(path) {
            !it.isDirectory && it.name.endsWith(".class")
                    && !SCAN_IGNORE_PREFIX.any { prefix -> it.path.startsWith(prefix) }
                    && !SCAN_IGNORE_NAME.contains(it.name)
        }

        val classes: MutableSet<Class<T>> = HashSet()
        for (resource in collection) {
            val nameByJar = convertClassName(resource.path)
            val nameByFile = convertClassName(path + resource.path)
            var cls: Class<T>? = try {
                loadClass(nameByJar, loaders) as Class<T>
            } catch (_: Throwable) {
                null
            }

            if (cls == null) {
                cls = try {
                    loadClass(nameByFile, loaders) as Class<T>
                } catch (e: Throwable) {
                    error.accept(nameByFile, e)
                    null
                }
            }
            if (cls != null && filter.test(cls)) {
                classes.add(cls)
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
            return emptyMap()
        }
        val map = HashMap<String, T>()
        for (field in fields(o.javaClass)) {
            if (filter.test(field)) {
                var value: Any? = null

                try {
                    value = field[o]
                } catch (_: IllegalAccessException) {
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
            val fields = ArrayList<Field>()
            while (k != null && !k.isAssignableFrom(Any::class.java)) {
                fields.addAll(k.declaredFields)
                k = k.superclass
            }
            fields.toTypedArray<Field>()
        }
    }

    @JvmStatic
    fun field(cls: Class<*>, name: String): Field? {
        return fields(cls).find { it.name == name }
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
            val fields: MutableList<ClassField> = ArrayList()
            while (k != null && !k.isAssignableFrom(Any::class.java)) {
                for (field in k.declaredFields) {
                    fields.add(ClassField(field.name, k))
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

    /**
     * 获取需要自动注入的字段(包括set 等其他需要参数的标识自动注入 方法)
     */
    @JvmStatic
    fun autowiredClassField(clazz: Class<*>): List<ClassField> {
        // 自动注入 - 字段
        val fields = fields(clazz).filter { !it.isFinal }
        // 自动注入 - 方法
        val methods = methods(clazz).filter { it.parameterCount > 0 }
        return (fields + methods).mapNotNull {
            val isAutowired =
                it.annotations.any { a -> AUTOWIRE_ANNOTATIONS.contains(a.annotationClass.qualifiedName) }
            if (!isAutowired) {
                return@mapNotNull null
            }
            val cf = if (it is Method) {
                ClassField(null, null, it)
            } else {
                ClassField(it as Field, null, null)
            }

            cf.visibleSet()
        }
    }

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
    @OptIn(ExperimentalContracts::class)
    fun isSuper(o: Any?, superClassName: String): Boolean {
        contract {
            returns(true) implies (o != null)
        }
        if (o == null) {
            return false
        }
        return isSuper(o.javaClass, superClassName)
    }

    @JvmStatic
    @OptIn(ExperimentalContracts::class)
    fun isSuper(cls: Class<*>?, superClassName: String): Boolean {
        contract {
            returns(true) implies (cls != null)
        }
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

    @JvmStatic
    @OptIn(ExperimentalContracts::class)
    fun isSuper(o: Any?, superClass: Class<*>): Boolean {
        contract {
            returns(true) implies (o != null)
        }
        if (o == null) {
            return false
        }
        return isSuper(o.javaClass, superClass)
    }

    /**
     * cls 是否是 superClass 或是其子类
     */
    @JvmStatic
    fun isSuper(cls: Class<*>, superClass: Class<*>): Boolean {
        if (superClass.isAssignableFrom(cls)) {
            return true
        }
        if (superClass == Any::class.java) {
            return true
        }
        if (cls == Any::class.java) {
            return false
        }
        return isSuper(cls, superClass.name)
    }

    @JvmStatic
    fun <T> constructors(cls: Class<T>): Array<Constructor<T>> {
        return CACHE_CONSTRUCTOR.computeIfAbsent(cls) { it.constructors } as Array<Constructor<T>>
    }

    @JvmField
    val AUTOWIRE_ANNOTATIONS = listOf(
        "org.springframework.beans.factory.annotation.Autowired",
        "jakarta.annotation.Resource",
        "javax.annotation.Resource"
    )

    @JvmStatic
    @JvmOverloads
    fun <T> newInstance(cls: Class<T>, autowired: Boolean = true, vararg args: Any??): T {
        return newInstance(constructors(cls)[0], autowired, args.toList())
    }

    @JvmStatic
    @JvmOverloads
    fun <T> newInstance(
        cls: Class<T>,
        autowired: Boolean = true,
        args: Collection<Any?>?,
        getArg: Function<Class<*>, Any?>? = null
    ): T {
        return newInstance(constructors(cls)[0], autowired, args, getArg)
    }

    @JvmStatic
    @JvmOverloads
    fun <T> newInstance(cls: Class<T>, autowired: Boolean = true, getArg: Function<Class<*>, Any?>): T {
        return newInstance(constructors(cls)[0], autowired, getArg)
    }


    @JvmStatic
    @JvmOverloads
    fun <T> newInstance(constructor: Constructor<T>, autowired: Boolean = true, vararg args: Any?): T {
        return newInstance(constructor, autowired, args.toList())
    }

    @JvmStatic
    @JvmOverloads
    fun <T> newInstance(
        constructor: Constructor<T>,
        autowired: Boolean = true,
        args: Collection<Any?>?,
        getArg: Function<Class<*>, Any?>? = null
    ): T {
        return newInstance(constructor, autowired) {
            if (args != null) {
                val f = args.firstOrNull { arg -> arg != null && isSuper(arg.javaClass, it) }
                if (f != null) {
                    return@newInstance f
                }
            }
            if (getArg != null) {
                return@newInstance getArg.apply(it)
            }
            return@newInstance null
        }
    }

    @JvmStatic
    @JvmOverloads
    fun <T> newInstance(constructor: Constructor<T>, autowired: Boolean = true, getArg: Function<Class<*>, Any?>): T {
        val types = constructor.parameterTypes
        val arguments = mutableListOf<Any?>()

        for (cls in types) {
            val argument = getArg.apply(cls)
            arguments.add(argument)
        }

        val t = constructor.newInstance(*arguments.toTypedArray())
        if (!autowired) {
            return t
        }
        val clazz = t::class.java
        val cfs = autowiredClassField(clazz)

        cfs.forEach { cf ->
            val classes = cf.getSetArgTypes()
            val args = classes.map { getArg.apply(it) }
            cf.set(t, *args.toTypedArray())
        }

        return t
    }

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
