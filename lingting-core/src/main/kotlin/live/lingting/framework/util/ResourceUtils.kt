package live.lingting.framework.util

import live.lingting.framework.resource.Resource
import live.lingting.framework.resource.ResourceResolverProvider
import java.util.function.Predicate

/**
 * @author lingting 2024-09-11 17:20
 */
object ResourceUtils {

    @JvmStatic
    @JvmOverloads
    fun get(name: String, loaders: Collection<ClassLoader> = ClassUtils.classLoaders()): Resource? {
        for (loader in loaders) {
            val url = loader.getResource(name)
            if (url == null) {
                continue
            }
            val resources = ResourceResolverProvider.resolve(url)
            val r = resources.firstOrNull {
                // 普通文件
                it.name == name
                        // jar中的文件
                        || it.link.endsWith(name)
                        // jar中的文件
                        || it.link.endsWith("$name/")
            }
            if (r != null) {
                return r
            }
        }

        return null
    }

    /**
     * 扫描资源
     * @param name      名称
     * @param predicate 是否返回该资源
     * @return 所有满足条件的资源对象
     */
    @JvmStatic
    @JvmOverloads
    fun scan(
        name: String,
        loaders: Collection<ClassLoader> = ClassUtils.classLoaders(),
        predicate: Predicate<Resource> = Predicate { true }
    ): List<Resource> {
        val result = LinkedHashSet<Resource>()

        loaders.forEach { loader ->
            val resources = loader.getResources(name)
            if (resources == null) {
                return@forEach
            }

            resources.asSequence().forEach { url ->
                val resources = ResourceResolverProvider.resolve(url, predicate)
                result.addAll(resources)
            }

        }

        return result.toList()
    }

}

