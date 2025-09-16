package live.lingting.framework.domain

import live.lingting.framework.util.StreamUtils
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class Resource(
    /**
     * 资源协议
     */
    val protocol: String,
    /**
     * 资源路径(根路径下的相对路径)
     */
    p: String,
    /**
     * 资源名称
     */
    val name: String,
    /**
     * 资源根路径, 表示文件从哪个文件(夹)开始识别到的
     */
    r: String,
    val isDirectory: Boolean
) {

    companion object {

        const val DELIMITER_PROTOCOL = ":/"

        const val DELIMITER_JAR: String = "!/"

        const val DELIMITER_FILE: String = "/"

        const val PROTOCOL_JAR: String = "jar"

        const val PROTOCOL_FILE: String = "file"

        @JvmStatic
        fun replace(source: String): String {
            return source.replace("\\", DELIMITER_FILE)
        }

    }

    constructor(protocol: String, file: File, root: String) : this(
        protocol, file.absolutePath.substring(root.length), file.name, root, file.isDirectory
    )

    constructor(protocol: String, file: File) : this(protocol, file, file.parentFile.absolutePath)

    val path = replace(p)

    val root = replace(r)

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

    @JvmOverloads
    fun string(charset: Charset = StandardCharsets.UTF_8): String {
        return StreamUtils.toString(stream(), charset)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Resource) return false
        return link == other.link;
    }

    override fun hashCode(): Int {
        return link.hashCode()
    }

    override fun toString(): String {
        return link
    }
}
