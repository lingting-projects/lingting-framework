package live.lingting.framework.resource

import java.io.File
import java.net.URI
import java.net.URL
import java.util.function.Predicate

/**
 * @author lingting 2025/10/17 10:07
 */
object FileResourceResolver : ResourceResolver {

    const val DELIMITER: String = "/"

    const val PROTOCOL: String = "file"

    override fun isSupport(u: URL, protocol: String): Boolean {
        return protocol.startsWith(PROTOCOL)
    }

    override fun resolve(u: URL, protocol: String, count: Int?, predicate: Predicate<Resource>): List<Resource> {
        if (count == 0) {
            return emptyList()
        }
        val uri = u.toURI()
        return resolve(uri, protocol, count, predicate)
    }

    fun resolve(uri: URI, protocol: String, count: Int?, predicate: Predicate<Resource>): List<Resource> {
        if (count == 0) {
            return emptyList()
        }
        val source = File(uri)
        return resolve(source, protocol, count, predicate)
    }

    fun resolve(source: File, protocol: String, count: Int?, predicate: Predicate<Resource>): List<Resource> {
        if (count == 0) {
            return emptyList()
        }
        val root = source.absolutePath
        val files = if (source.isDirectory) {
            source.walk()
        } else {
            sequenceOf(source)
        }

        return files
            .map { Resource(protocol, it, root) }
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

}
