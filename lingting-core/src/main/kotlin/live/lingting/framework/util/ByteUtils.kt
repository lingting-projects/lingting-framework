package live.lingting.framework.util

/**
 * @author lingting 2023-12-22 11:28
 */
object ByteUtils {
    @JvmStatic
    fun toArray(list: List<Byte>): ByteArray {
        val bytes = ByteArray(list.size)
        for (i in list.indices) {
            bytes[i] = list[i]
        }
        return bytes
    }

    /**
     * 两个字节是否表示行尾
     *
     *
     * 字节1在字节2前面
     *
     *
     * @param byte1 字节1
     * @param byte2 字节2
     * @return true 行尾
     */
    @JvmStatic
    fun isEndLine(byte1: Byte, byte2: Byte): Boolean {
        return byte1 == '\r'.code.toByte() && byte2 == '\n'.code.toByte()
    }

    /**
     * 字节是否表示行尾
     *
     * @param byte1 字节1
     * @return true 行尾
     */
    @JvmStatic
    fun isEndLine(byte1: Byte): Boolean {
        return byte1 == '\n'.code.toByte()
    }

    /**
     * 此数据是否为完整的一行数据(以换行符结尾)
     *
     * @param bytes 字节
     * @return true 一整行
     */
    @JvmStatic
    fun isLine(bytes: List<Byte>): Boolean {
        val size = bytes.size
        if (size < 1) {
            return false
        }

        val last = bytes.last()
        if (isEndLine(last)) {
            return true
        }
        if (size > 1) {
            val penultimate = bytes[size - 2]
            return isEndLine(penultimate, last)
        }
        return false
    }

    /**
     * 此数据是否为完整的一行数据(以换行符结尾)
     *
     * @param bytes 字节
     * @return true 一整行
     */
    @JvmStatic
    fun isLine(bytes: ByteArray): Boolean {
        val size = bytes.size
        if (size < 1) {
            return false
        }

        val last = bytes[size - 1]
        if (isEndLine(last)) {
            return true
        }
        if (size > 1) {
            val penultimate = bytes[size - 2]
            return isEndLine(penultimate, last)
        }
        return false
    }

    /**
     * 移除行数据中的行尾符合
     *
     * @param list 数据
     * @return 移除后的数据, 如果没有则是原数据
     */
    @JvmStatic
    fun trimEndLine(list: List<Byte>): ByteArray {
        if (CollectionUtils.isEmpty(list)) {
            return ByteArray(0)
        }

        val lastIndex = list.size - 1
        val last = list[lastIndex]

        // 大于2字节
        if (list.size > 1) {
            val penultimateIndex = list.size - 2
            val penultimate = list[penultimateIndex]

            // 如果是 2字节表示行尾
            if (isEndLine(penultimate, last)) {
                return toArray(list.subList(0, penultimateIndex))
            }
        }

        // 1字节表示行尾
        if (isEndLine(last)) {
            return toArray(list.subList(0, lastIndex))
        }

        // 如果没有行尾符
        return toArray(list)
    }
}
