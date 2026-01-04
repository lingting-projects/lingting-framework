package live.lingting.framework.resource

import live.lingting.framework.Sequence
import live.lingting.framework.util.StringUtils
import java.net.URL
import java.util.ServiceConfigurationError
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

/**
 * @author lingting 2025/10/17 10:01
 */
object ResourceResolverProvider {

    const val DELIMITER = ":/"

    private var list = listOf<ResourceResolver>()

    private val source: ConcurrentHashMap<ResourceResolver, Boolean> =
        ConcurrentHashMap<ResourceResolver, Boolean>().apply {
            try {
                val loaders = ServiceLoader.load(ResourceResolver::class.java)
                for (resolver in loaders) {
                    if (resolver != null) {
                        put(resolver, true)
                    }
                }
            } catch (_: ServiceConfigurationError) {
                //
            }
            put(FileResourceResolver, true)
            put(JarResourceResolver, true)
            // jvm 初始化类时, 字段赋值是严格按照顺序的, list初始值是emptyList().
            // 如果source字段声明在list前面, 那么reorder赋值的数据会在list初始化时被覆盖. 因为source要先于list初始化
            reorder(this)
        }

    /**
     * 升序排序
     */
    @JvmStatic
    fun reorder() {
        reorder(source)
    }

    @JvmStatic
    private fun reorder(map: Map<ResourceResolver, Boolean>) {
        val resolvers = map.filter { it.value }.map { it.key }
        this.list = Sequence.asc(resolvers)
    }

    @JvmStatic
    fun register(resolver: ResourceResolver) {
        source[resolver] = true
        reorder()
    }

    @JvmStatic
    fun unregister(resolver: ResourceResolver) {
        source.remove(resolver)
        reorder()
    }

    /**
     * 解析资源
     * @param count 最多分析多少个资源就结束, null 或小于0则无限 0 为空
     */
    @JvmStatic
    @JvmOverloads
    fun resolve(u: URL, count: Int? = null, predicate: Predicate<Resource> = Predicate { true }): List<Resource> {
        if (count == 0) {
            return emptyList()
        }
        val url = u.toString()
        val protocol = StringUtils.substringBefore(url, DELIMITER)
        val resolver = list.firstOrNull { it.isSupport(u, protocol) }
        if (resolver == null) {
            return listOf()
        }
        return resolver.resolve(u, protocol, count, predicate)
    }

}
