package live.lingting.framework.resource

import java.net.JarURLConnection
import java.net.URL
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

    override fun resolve(u: URL, protocol: String): List<Resource> {
        val paths = u.toString().split(DELIMITER).dropLastWhile { it.isEmpty() }
        val jarPaths = if (paths.size == 1) paths else paths.dropLast(1)
        val jarPath = jarPaths.joinToString(DELIMITER)
            .substring(protocol.length + ResourceResolverProvider.DELIMITER.length)
            .let { it + DELIMITER }
        val connection = u.openConnection() as JarURLConnection
        val entry = connection.jarEntry
        // 如果连接指向单个文件, 那么只加载这一个, 不扫描其他的. 只有指向jar才扫描全部
        return if (entry != null) {
            // 文件夹下的所有文件, 包括所有层级的子文件夹内的文件
            if (entry.isDirectory) {
                fromJar(connection, protocol, jarPath, entry)
            } else {
                listOf(fromEntry(entry, protocol, jarPath))
            }
        } else {
            fromJar(connection, protocol, jarPath)
        }
    }

    fun fromJar(
        connection: JarURLConnection,
        protocol: String,
        jarPath: String,
        dir: JarEntry? = null
    ): List<Resource> {
        val list = mutableListOf<Resource>()
        val jarFile = connection.jarFile
        jarFile.entries().asSequence().forEach { entry ->
            if (dir == null || entry.name.startsWith(dir.name)) {
                val of = fromEntry(entry, protocol, jarPath)
                list.add(of)
            }
        }
        return list
    }

    fun fromEntry(entry: JarEntry, protocol: String, jarPath: String): Resource {
        val name = entry.name.split("/").dropLastWhile { it.isEmpty() }.last()
        val of = Resource(protocol, entry.name, name, jarPath, entry.isDirectory)
        return of
    }
}
