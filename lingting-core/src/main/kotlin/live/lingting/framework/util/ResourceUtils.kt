package live.lingting.framework.util

import live.lingting.framework.domain.Resource
import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.Files
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
            val r = resolver(url).firstOrNull {
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

            while (resources.hasMoreElements()) {
                val url = resources.nextElement()
                val resolver = resolver(url, predicate)
                result.addAll(resolver)
            }

        }

        return result.toList()
    }

    @JvmStatic
    @JvmOverloads
    fun resolver(url: URL, predicate: Predicate<Resource> = Predicate { true }): List<Resource> {
        val root = url.toString()
        val protocol = StringUtils.substringBefore(root, Resource.DELIMITER_PROTOCOL)
        val result = ArrayList<Resource>()
        if (protocol.startsWith(Resource.PROTOCOL_FILE)) {
            fillByFile(url, protocol, predicate, result)
        } else if (protocol.startsWith(Resource.PROTOCOL_JAR)) {
            fillByJar(url, root, protocol, predicate, result)
        }
        return result
    }

    private fun fillByJar(
        url: URL,
        root: String,
        protocol: String,
        predicate: Predicate<Resource>,
        result: ArrayList<Resource>
    ) {
        val connection = url.openConnection()
        if (connection is JarURLConnection) {
            val jarPaths = root.split(Resource.DELIMITER_JAR).dropLastWhile { it.isEmpty() }.dropLast(1)
            val jarPath = if (jarPaths.isEmpty()) "" else {
                jarPaths.joinToString(Resource.DELIMITER_JAR)
                    .substring(protocol.length + Resource.DELIMITER_PROTOCOL.length)
                    .let { it + Resource.DELIMITER_JAR }
            }
            val jarFile = connection.jarFile
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val name = entry.name.split("/").dropLastWhile { it.isEmpty() }.last()
                val of = Resource(protocol, entry.name, name, jarPath, entry.isDirectory)
                if (predicate.test(of)) {
                    result.add(of)
                }
            }
        }
    }

    private fun fillByFile(url: URL, protocol: String, predicate: Predicate<Resource>, result: ArrayList<Resource>) {
        val uri = url.toURI()
        val source = File(uri)
        val root = source.absolutePath
        val files = if (source.isDirectory) {
            ArrayList<File>().apply {
                Files.walk(source.toPath()).use {
                    it.forEach { p -> add(p.toFile()) }
                }
            }
        } else {
            listOf(source)
        }

        files.forEach {
            val of = Resource(protocol, it, root)
            if (predicate.test(of)) {
                result.add(of)
            }
        }
    }

}

