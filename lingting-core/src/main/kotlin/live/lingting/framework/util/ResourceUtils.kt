package live.lingting.framework.util

import java.io.File
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.Arrays
import java.util.Collections
import java.util.function.Predicate
import java.util.stream.Collectors
import live.lingting.framework.function.ThrowingSupplier

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
        val resource = loader.getResource(name) ?: return null
        val url = resource.toString()
        val protocol: String = StringUtils.substringBefore(url, ":/")
        if (protocol.startsWith(Resource.PROTOCOL_FILE)) {
            val uri = resource.toURI()
            val dir = File(uri)
            return Resource.of(protocol, dir)
        }

        if (protocol.startsWith(Resource.PROTOCOL_JAR)) {
            val connection = resource.openConnection()
            if (connection is JarURLConnection) {
                val paths: Collection<String> = url.split(Resource.DELIMITER_JAR).dropLast(1)
                val entry = connection.jarEntry
                return Resource(protocol, paths, entry.name, entry.isDirectory)
            }
        }
        return null
    }

    /**
     * 扫描资源
     *
     * @param name      名称
     * @param predicate 是否返回该资源
     * @return 所有满足条件的资源对象
     */
    @JvmStatic
    @JvmOverloads
    fun scan(name: String, predicate: Predicate<Resource> = Predicate { resource -> true }): Collection<Resource> {
        val loader = currentClassLoader()
        return scan(loader, name, predicate)
    }

    @JvmStatic
    fun scan(loader: ClassLoader, name: String, predicate: Predicate<Resource>): Collection<Resource> {
        val resources = loader.getResources(name)
        val result: MutableCollection<Resource> = LinkedHashSet()
        while (resources.hasMoreElements()) {
            val url = resources.nextElement()
            handler(result, url, predicate)
        }
        return result
    }

    @JvmStatic
    fun handler(result: MutableCollection<Resource>, resource: URL, predicate: Predicate<Resource>) {
        val url = resource.toString()
        val protocol: String = StringUtils.substringBefore(url, ":/")

        if (protocol.startsWith(Resource.PROTOCOL_FILE)) {
            val uri = resource.toURI()
            val dir = File(uri)
            if (!dir.isDirectory) {
                fill(result, ThrowingSupplier { Resource.of(protocol, dir) }, predicate)
                return
            }
            Files.walk(dir.toPath()).use { walk ->
                walk.forEach { path -> fill(result, ThrowingSupplier { Resource.of(protocol, path.toFile()) }, predicate) }
            }
            return
        }

        if (protocol.startsWith(Resource.PROTOCOL_JAR)) {
            val connection = resource.openConnection()
            if (connection is JarURLConnection) {
                val split: Array<String> = url.split(Resource.DELIMITER_JAR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val size = split.size - 1
                val paths: Collection<String> = Arrays.stream(split).limit(size.toLong()).toList()
                val file = connection.jarFile
                val entries = file.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    fill(result, ThrowingSupplier { Resource(protocol, paths, entry.name, entry.isDirectory) }, predicate)
                }
            }
        }
    }


    @JvmStatic
    fun fill(
        resources: MutableCollection<Resource>, supplier: ThrowingSupplier<Resource>,
        predicate: Predicate<Resource>
    ) {
        val resource = supplier.get()
        if (predicate.test(resource)) {
            resources.add(resource)
        }
    }

}

class Resource(val protocol: String, paths: Collection<String>, val name: String, val isDirectory: Boolean) {
    val paths: Collection<String> = Collections.unmodifiableCollection(paths)

    val isJar: Boolean = PROTOCOL_JAR.startsWith(protocol)

    val isFile: Boolean = PROTOCOL_FILE.startsWith(protocol)

    val delimiter: String = if (this.isJar) DELIMITER_JAR else DELIMITER_FILE

    val path: String

    val uri: URI by lazy { URI.create(path) }

    val url: URL by lazy { uri.toURL() }

    init {
        val suffix = if (isJar && !name.startsWith("/")) "/$name" else name
        this.path = this.paths.stream().collect(Collectors.joining(DELIMITER_FILE, "", delimiter + suffix))
    }

    fun file(): File {
        return File(uri)
    }

    fun stream(): InputStream {
        return url.openStream()
    }

    companion object {

        const val DELIMITER_JAR: String = "!"

        const val DELIMITER_FILE: String = "/"

        const val PROTOCOL_JAR: String = "jar"

        const val PROTOCOL_FILE: String = "file"

        @JvmStatic
        fun of(protocol: String, file: File): Resource {
            val paths = Arrays
                .stream<String>(file.parentFile.absoluteFile.toURI().toString().split(DELIMITER_FILE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                .toList()
            return Resource(protocol, paths, file.name, file.isDirectory)
        }
    }
}

