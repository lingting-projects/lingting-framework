package live.lingting.framework.util

import java.io.CharArrayWriter
import java.util.*

/**
 * @author lingting
 */
class StringUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val BOM_UTF8: String = "\uFEFF"

        const val BOM_UTF16B: String = "\uFEFF"

        const val BOM_UTF16S: String = "\uFFFE"

        const val BOM_UTF32B: String = "\u0000FEFF"

        const val BOM_UTF32S: String = "\uFFFFE0000"

        /**
         * 指定字符串是否存在可见字符
         *
         * @param str 字符串
         * @return boolean
         */

        fun hasText(str: CharSequence?): Boolean {
            if (str == null || str.isEmpty()) {
                return false
            }

            for (i in 0 until str.length) {
                // 如果是非空白字符
                if (!Character.isWhitespace(str[i])) {
                    return true
                }
            }

            return false
        }

        fun join(iterable: Iterable<*>?, delimiter: String?): String? {
            if (iterable == null) {
                return null
            }

            return join(iterable.iterator(), delimiter)
        }

        fun join(iterator: Iterator<*>?, delimiter: String?): String? {
            if (iterator == null) {
                return null
            }

            val builder = StringBuilder()
            while (iterator.hasNext()) {
                val next = iterator.next() ?: continue
                builder.append(next)

                if (iterator.hasNext()) {
                    builder.append(delimiter)
                }
            }

            return builder.toString()
        }


        fun firstLower(str: String): String? {
            if (!hasText(str)) {
                return str
            }

            val c = str[0]
            if (CharUtils.isUpperLetter(c)) {
                return c.lowercaseChar().toString() + str.substring(1)
            }
            return str
        }

        fun firstUpper(str: String): String? {
            if (!hasText(str)) {
                return str
            }

            val c = str[0]
            if (CharUtils.isLowerLetter(c)) {
                return c.uppercaseChar().toString() + str.substring(1)
            }
            return str
        }

        /**
         * 驼峰字符串转下划线字符串
         *
         *
         * eg: HumpToUnderscore -> hump_to_underscore
         *
         */

        fun humpToUnderscore(str: String): String {
            val writer = CharArrayWriter()
            for (i in 0 until str.length) {
                val c = str[i]
                // 大写字母处理
                if (CharUtils.isUpperLetter(c)) {
                    // 如果不是第一个大写字母, 插入下划线 _
                    if (writer.size() > 0) {
                        writer.append('_')
                    }
                    // 转小写
                    writer.append(c.lowercaseChar())
                } else {
                    writer.append(c)
                }
            }

            return writer.toString()
        }

        /**
         * 下划线字符串转驼峰字符串
         *
         *
         * eg: HumpToUnderscore -> hump_to_underscore
         *
         */

        fun underscoreToHump(str: String): String {
            val writer = CharArrayWriter()
            var upper = false
            for (i in 0 until str.length) {
                val c = str[i]
                // 如果是下划线, 下一个要转大写
                if (c == '_') {
                    upper = true
                    continue
                }
                // 要转大写
                if (upper) {
                    writer.append(c.uppercaseChar())
                    upper = false
                } else {
                    writer.append(c)
                }
            }

            return writer.toString()
        }


        fun hex(bytes: ByteArray): String {
            val builder = StringBuilder()

            for (b in bytes) {
                val hex = Integer.toHexString((b.toInt() and 0xFF) or 0x100)
                builder.append(hex, 1, 3)
            }

            return builder.toString()
        }


        fun hex(hex: String): ByteArray {
            val length = hex.length
            require(length % 2 == 0) { "Invalid hexadecimal string" }

            val byteArray = ByteArray(length / 2)
            var i = 0
            while (i < length) {
                // 16进制单字符
                val h: String = hex.substring(i, i + 2)
                // 转byte
                val b = h.toInt(16) as Byte
                byteArray[i / 2] = b
                i += 2
            }

            return byteArray
        }

        /**
         * 字节码转base64字符串
         */

        fun base64(bytes: ByteArray?): String {
            return Base64.getEncoder().encodeToString(bytes)
        }

        /**
         * base64字符串转字节码
         */

        fun base64(base64: String?): ByteArray {
            return Base64.getDecoder().decode(base64)
        }

        /**
         * 往前缀追加 指定数量的指定字符
         *
         * @param prefix 前缀
         * @param count  数量
         * @param str    指定字符
         * @return 追加完成后的字符串
         */
        fun append(prefix: String, count: Int, str: String): String {
            return prefix + str.repeat(max(0, count))
        }

        fun cleanBom(string: String): String {
            return string.replace(BOM_UTF32S, "")
                .replace(BOM_UTF32B, "")
                .replace(BOM_UTF16S, "")
                .replace(BOM_UTF16B, "")
                .replace(BOM_UTF8, "")
        }


        fun substringBefore(str: String, separator: String): String {
            val pos: Int = str.indexOf(separator)
            return if (pos == -1) str else str.substring(0, pos)
        }


        fun substringBeforeLast(str: String, separator: String): String {
            val pos: Int = str.lastIndexOf(separator)
            return if (pos == -1) str else str.substring(0, pos)
        }


        fun substringAfter(str: String, separator: String): String {
            val pos: Int = str.indexOf(separator)
            return if (pos == -1) "" else str.substring(pos + separator.length)
        }


        fun substringAfterLast(str: String, separator: String): String {
            val pos: Int = str.lastIndexOf(separator)
            return if (pos == -1) str else str.substring(pos + separator.length)
        }


        fun deleteLast(builder: StringBuilder?): StringBuilder? {
            if (builder == null || builder.isEmpty()) {
                return builder
            }
            val index = builder.length - 1
            return builder.deleteCharAt(index)
        }
    }
}
