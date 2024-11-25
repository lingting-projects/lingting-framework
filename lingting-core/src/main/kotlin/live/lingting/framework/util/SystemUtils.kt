package live.lingting.framework.util

import java.io.File
import java.nio.charset.Charset
import live.lingting.framework.function.ThrowableSupplier
import live.lingting.framework.value.LazyValue

/**
 * @author lingting 2022/6/25 12:10
 */
object SystemUtils {

    /**
     * 当前系统是否为Windows系统, 参考以下系统API
     * @return boolean
     * @see sun.awt.OSInfo.getOSType
     */
    @JvmStatic
    val isWindows: Boolean
        get() = osName().contains("Windows")

    @JvmStatic
    val isLinux: Boolean
        get() = osName().contains("Linux")

    @JvmStatic
    val isMacX: Boolean
        get() = osName().contains("OS X")

    @JvmStatic
    val isMac: Boolean
        get() = osName().contains("Mac OS")

    @JvmStatic
    val isAix: Boolean
        get() = osName().contains("AIX")

    @JvmStatic
    fun osName(): String {
        return System.getProperty("os.name")
    }

    @JvmStatic
    val charset: LazyValue<Charset> = LazyValue(ThrowableSupplier {
        try {
            val name = System.getProperty("sun.jnu.encoding")
            return@ThrowableSupplier Charset.forName(name)
        } catch (_: Exception) {
            return@ThrowableSupplier Charset.defaultCharset()
        }
    })

    /**
     * 获取系统字符集
     */
    @JvmStatic
    fun charset(): Charset {
        return charset.get()!!
    }

    @JvmStatic
    fun lineSeparator(): String {
        return System.lineSeparator()
    }

    @JvmStatic
    fun fileSeparator(): String {
        return File.separator
    }

    @JvmStatic
    fun tmpDir(): File {
        return File(System.getProperty("java.io.tmpdir"))
    }

    @JvmStatic
    fun tmpDirLingting(): File {
        return File(System.getProperty("java.io.tmpdir"), "lingting")
    }

    @JvmStatic
    fun homeDir(): File {
        return File(System.getProperty("user.home"))
    }

    @JvmStatic
    fun homeDirLingting(): File {
        return File(System.getProperty("user.home"), ".lingting")
    }

    @JvmStatic
    fun workDir(): File {
        return File(System.getProperty("user.dir"))
    }

    @JvmStatic
    fun username(): String {
        return System.getProperty("user.name")
    }

    @JvmStatic
    fun javaVersion(): String {
        return System.getProperty("java.version")
    }

    @JvmStatic
    fun proxy(host: String, port: Int) {
        proxyHttp(host, port)
        proxyHttps(host, port)
    }

    @JvmStatic
    fun proxyHttp(host: String, port: Int) {
        System.setProperty("http.proxyHost", host)
        System.setProperty("http.proxyPort", port.toString())
    }

    @JvmStatic
    fun proxyHttps(host: String, port: Int) {
        System.setProperty("https.proxyHost", host)
        System.setProperty("https.proxyPort", port.toString())
    }
}

