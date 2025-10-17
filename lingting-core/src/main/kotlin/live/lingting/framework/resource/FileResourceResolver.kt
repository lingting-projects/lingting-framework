package live.lingting.framework.resource

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Files

/**
 * @author lingting 2025/10/17 10:07
 */
object FileResourceResolver : ResourceResolver {

    const val DELIMITER: String = "/"

    const val PROTOCOL: String = "file"

    override fun isSupport(u: URL, protocol: String): Boolean {
        return protocol.startsWith(PROTOCOL)
    }

    override fun resolve(u: URL, protocol: String): List<Resource> {
        val uri = u.toURI()
        return resolve(uri, protocol)
    }

    fun resolve(uri: URI, protocol: String): MutableList<Resource> {
        val source = File(uri)
        return resolve(source, protocol)
    }

    fun resolve(source: File, protocol: String): MutableList<Resource> {
        val list = mutableListOf<Resource>()
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
            list.add(of)
        }

        return list
    }

}
