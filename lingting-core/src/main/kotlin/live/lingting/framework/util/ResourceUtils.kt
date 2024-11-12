package live.lingting.framework.util

import live.lingting.framework.function.ThrowingSupplier
import java.io.File
import java.io.InputStream
import java.net.JarURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * @author lingting 2024-09-11 17:20
 */
class ResourceUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    class Resource(val protocol: String, paths: Collection<String>, val name: String, val isDirectory: Boolean) {
        val paths: Collection<String> = Collections.unmodifiableCollection(paths)

        val isJar: Boolean

        val isFile: Boolean

        val delimiter: String

        val path: String

        protected var uri: URI? = null

        protected var url: URL? = null

        init {
            this.isJar = PROTOCOL_JAR.startsWith(protocol)
            this.isFile = PROTOCOL_FILE.startsWith(protocol)
            this.delimiter = if (this.isJar) DELIMITER_JAR else DELIMITER_FILE
            val suffix = if (isJar && !name.startsWith("/")) "/$name" else name
            this.path = this.paths.stream().collect(Collectors.joining(DELIMITER_FILE, "", delimiter + suffix))
        }

        fun getUri(): URI {
            if (uri == null) {
                uri = URI.create(path)
            }
            return uri!!
        }

        fun uri(): URI {
            return getUri()
        }


        fun getUrl(): URL {
            if (url == null) {
                url = getUri().toURL()
            }
            return url!!
        }


        fun url(): URL {
            return getUrl()
        }

        fun file(): File {
            return File(getUri())
        }


        fun stream(): InputStream {
            return getUrl().openStream()
        }

        override fun hashCode(): Int {
            return path.hashCode()
        }

        override fun equals(obj: Any?): Boolean {
            return obj is Resource && obj.path == path
        }

        companion object {
            const val DELIMITER_JAR: String = "!"

            const val DELIMITER_FILE: String = "/"

            const val PROTOCOL_JAR: String = "jar"

            const val PROTOCOL_FILE: String = "file"

            fun of(protocol: String, file: File): Resource {
                val paths = Arrays
                    .stream<String>(file.parentFile.absoluteFile.toURI().toString().split(DELIMITER_FILE.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                    .toList()
                return Resource(protocol, paths, file.name, file.isDirectory)
            }
        }
    }

    companion object {
        fun currentClassLoader(): ClassLoader {
            val loader = Thread.currentThread().contextClassLoader
            return loader ?: ClassLoader.getSystemClassLoader()
        }


        fun get(name: String?): Resource? {
            val loader = currentClassLoader()
            return get(loader, name)
        }


        fun get(loader: ClassLoader, name: String?): Resource? {
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
                    val split: Array<String> = url.split(Resource.DELIMITER_JAR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val size = split.size - 1
                    val paths: Collection<String> = Arrays.stream(split).limit(size.toLong()).toList()
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


        fun scan(name: String?, predicate: Predicate<Resource?> = Predicate { resource: Resource? -> true }): Collection<Resource> {
            val loader = currentClassLoader()
            return scan(loader, name, predicate)
        }


        fun scan(loader: ClassLoader, name: String?, predicate: Predicate<Resource?>): Collection<Resource> {
            val resources = loader.getResources(name)
            val result: MutableCollection<Resource> = LinkedHashSet()
            while (resources.hasMoreElements()) {
                val url = resources.nextElement()
                handler(result, url, predicate)
            }
            return result
        }


        fun handler(result: MutableCollection<Resource>, resource: URL, predicate: Predicate<Resource?>) {
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
                    walk.forEach { path: Path -> fill(result, ThrowingSupplier { Resource.of(protocol, path.toFile()) }, predicate) }
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


        fun fill(
            resources: MutableCollection<Resource?>, supplier: ThrowingSupplier<Resource?>,
            predicate: Predicate<Resource?>
        ) {
            val resource = supplier.get()
            if (predicate.test(resource)) {
                resources.add(resource)
            }
        }
    }
}
