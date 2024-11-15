package live.lingting.framework.util

import java.io.File
import java.nio.charset.Charset
import live.lingting.framework.function.ThrowableSupplier
import live.lingting.framework.value.LazyValue

/**
 * @author lingting 2022/6/25 12:10
 */
class SystemUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {

        val isWindows: Boolean
            /**
             * 当前系统是否为Windows系统, 参考以下系统API
             *
             * @return boolean
             * @see sun.awt.OSInfo.getOSType
             */
            get() = osName().contains("Windows")

        val isLinux: Boolean
            get() = osName().contains("Linux")

        val isMacX: Boolean
            get() = osName().contains("OS X")

        val isMac: Boolean
            get() = osName().contains("Mac OS")

        val isAix: Boolean
            get() = osName().contains("AIX")

        fun osName(): String {
            return System.getProperty("os.name")
        }

        val charset: LazyValue<Charset> = LazyValue(ThrowableSupplier {
            try {
                val name = System.getProperty("sun.jnu.encoding")
                return@ThrowableSupplier Charset.forName(name)
            } catch (e: Exception) {
                return@ThrowableSupplier Charset.defaultCharset()
            }
        })

        /**
         * 获取系统字符集
         */
        fun charset(): Charset {
            return charset.get()!!
        }

        fun lineSeparator(): String {
            return System.lineSeparator()
        }

        fun fileSeparator(): String {
            return File.separator
        }

        fun tmpDir(): File {
            return File(System.getProperty("java.io.tmpdir"))
        }

        fun tmpDirLingting(): File {
            return File(System.getProperty("java.io.tmpdir"), "lingting")
        }

        fun homeDir(): File {
            return File(System.getProperty("user.home"))
        }

        fun homeDirLingting(): File {
            return File(System.getProperty("user.home"), ".lingting")
        }

        fun workDir(): File {
            return File(System.getProperty("user.dir"))
        }

        fun username(): String {
            return System.getProperty("user.name")
        }

        fun javaVersion(): String {
            return System.getProperty("java.version")
        }

        fun proxy(host: String, port: Int) {
            proxyHttp(host, port)
            proxyHttps(host, port)
        }

        fun proxyHttp(host: String, port: Int) {
            System.setProperty("http.proxyHost", host)
            System.setProperty("http.proxyPort", port.toString())
        }

        fun proxyHttps(host: String, port: Int) {
            System.setProperty("https.proxyHost", host)
            System.setProperty("https.proxyPort", port.toString())
        }
    }
}
