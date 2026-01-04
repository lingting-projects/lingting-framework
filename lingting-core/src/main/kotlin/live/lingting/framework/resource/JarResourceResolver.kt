package live.lingting.framework.resource

import java.net.JarURLConnection
import java.net.URL
import java.util.function.Predicate
import java.util.jar.JarEntry

/**
 * @author lingting 2025/10/17 10:07
 */
object JarResourceResolver : ResourceResolver {

    const val DELIMITER: String = "!/"

    const val PROTOCOL: String = "jar"

    override fun isSupport(u: URL, protocol: String): Boolean {
        if (!protocol.startsWith(PROTOCOL)) {
            return false
        }
        val connection = u.openConnection()
        return connection is JarURLConnection
    }

    override fun resolve(u: URL, protocol: String, count: Int?, predicate: Predicate<Resource>): List<Resource> {
        if (count == 0) {
            return emptyList()
        }
        val paths = u.toString().split(DELIMITER).dropLastWhile { it.isEmpty() }
        val jarPaths = if (paths.size == 1) paths else paths.dropLast(1)
        val jarPath = jarPaths.joinToString(DELIMITER)
            .substring(protocol.length + ResourceResolverProvider.DELIMITER.length)
            .let { it + DELIMITER }
        val connection = u.openConnection() as JarURLConnection
        val entry = connection.jarEntry
        // 如果连接指向单个文件, 那么只加载这一个, 不扫描其他的. 只有指向jar才扫描全部
        val resources: Sequence<Resource> = if (entry != null) {
            if (entry.isDirectory) {
                fromJar(connection, protocol, jarPath, entry)
            } else {
                sequenceOf(fromEntry(entry, protocol, jarPath))
            }
        } else {
            fromJar(connection, protocol, jarPath)
        }
        return resources
            .filter { predicate.test(it) }
            .let {
                if (count == null || count < 1) {
                    it
                } else {
                    it.take(count)
                }
            }
            .toList()
    }

    fun fromJar(
        connection: JarURLConnection,
        protocol: String,
        jarPath: String,
        dir: JarEntry? = null
    ): Sequence<Resource> {
        val jarFile = connection.jarFile
        return jarFile.entries().asSequence()
            .filter { dir == null || it.name.startsWith(dir.name) }
            .map { fromEntry(it, protocol, jarPath) }
    }

    fun fromEntry(entry: JarEntry, protocol: String, jarPath: String): Resource {
        val name = entry.name.split("/").dropLastWhile { it.isEmpty() }.last()
        val of = Resource(protocol, entry.name, name, jarPath, entry.isDirectory)
        return of
    }
}
