package live.lingting.framework.util

import java.io.File
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.util.function.Predicate

/**
 * @author lingting 2024-09-11 17:20
 */
object ResourceUtils {

    @JvmStatic
    fun currentClassLoader(): ClassLoader {
        val loader = Thread.currentThread().contextClassLoader
        return loader ?: ClassLoader.getSystemClassLoader()
    }

    @JvmStatic
    fun get(name: String): Resource? {
        val loader = currentClassLoader()
        return get(loader, name)
    }

    @JvmStatic
    fun get(loader: ClassLoader, name: String): Resource? {
        val url = loader.getResource(name) ?: return null
        val resources = resolver(url)
        return resources.firstOrNull()
    }

    /**
     * 扫描资源
     * @param name      名称
     * @param predicate 是否返回该资源
     * @return 所有满足条件的资源对象
     */
    @JvmStatic
    @JvmOverloads
    fun scan(name: String, predicate: Predicate<Resource> = Predicate { true }): List<Resource> {
        val loader = currentClassLoader()
        return scan(loader, name, predicate)
    }

    @JvmStatic
    fun scan(loader: ClassLoader, name: String, predicate: Predicate<Resource>): List<Resource> {
        val resources = loader.getResources(name)
        val result = LinkedHashSet<Resource>()
        while (resources.hasMoreElements()) {
            val url = resources.nextElement()
            val resolver = resolver(url, predicate)
            result.addAll(resolver)
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

    private fun fillByJar(url: URL, root: String, protocol: String, predicate: Predicate<Resource>, result: ArrayList<Resource>) {
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
        val root = Resource.convertPath(source.absolutePath)
        val files = if (source.isDirectory) {
            ArrayList<File>().apply {
                Files.walk(source.toPath()).use {
                    it.forEach { add(it.toFile()) }
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

class Resource(
    /**
     * 资源协议
     */
    val protocol: String,
    /**
     * 资源路径(根路径下的相对路径)
     */
    val path: String,
    /**
     * 资源名称
     */
    val name: String,
    /**
     * 资源根路径, 表示文件从哪个文件(夹)开始识别到的
     */
    val root: String,
    val isDirectory: Boolean
) {

    companion object {

        const val DELIMITER_PROTOCOL = ":/"

        const val DELIMITER_JAR: String = "!/"

        const val DELIMITER_FILE: String = "/"

        const val PROTOCOL_JAR: String = "jar"

        const val PROTOCOL_FILE: String = "file"

        @JvmStatic
        fun convertPath(source: String): String {
            return source.replace("\\", DELIMITER_FILE)
        }

    }

    constructor(protocol: String, file: File, root: String) : this(
        protocol, convertPath(file.absolutePath.substring(root.length)), file.name, root, file.isDirectory
    )

    /**
     * 资源本身是文件
     */
    val isFile = !isDirectory

    /**
     * 资源来源是jar包
     */
    val fromJar: Boolean = protocol.startsWith(PROTOCOL_JAR)

    /**
     * 资源来源是文件
     */
    val fromFile: Boolean = protocol.startsWith(PROTOCOL_FILE)

    /**
     * 资源链接 - 包含协议和路径
     */
    val link = protocol + DELIMITER_PROTOCOL + root + path

    val uri: URI by lazy { URI.create(link) }

    val url: URL by lazy { uri.toURL() }

    fun file(): File {
        return File(uri)
    }

    fun stream(): InputStream {
        return url.openStream()
    }

    fun string(): String {
        return StreamUtils.toString(stream())
    }

    override fun toString(): String {
        return link
    }
}

