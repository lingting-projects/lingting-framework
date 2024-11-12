package live.lingting.framework.util

import java.util.concurrent.ThreadLocalRandom

/**
 * @author lingting
 */
class RandomUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 用于随机选的数字
         */
        const val NUMBER: String = "0123456789"

        /**
         * 用于随机选的字母
         */
        const val LETTER: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

        /**
         * 用于生成16进制
         */
        const val HEX: String = "0123456789ABCEDF"

        /**
         * 用于随机选的字符
         */
        const val STRING: String = NUMBER + LETTER

        val random: ThreadLocalRandom
            get() = ThreadLocalRandom.current()

        /**
         * 随机数
         *
         * @param max 最大值 - 不包括该值
         * @return int
         */
        fun nextInt(max: Int): Int {
            return nextInt(0, max)
        }

        /**
         * 随机数
         *
         * @param min 最小值 - 包括该值
         * @param max 最大值 - 不包括该值
         * @return int
         */
        fun nextInt(min: Int, max: Int): Int {
            return random.nextInt(min, max)
        }

        /**
         * 从指定字符串中随机生成字符串
         *
         * @param base 根字符
         * @param len  长度
         * @return java.lang.String
         */
        fun nextStr(base: String, len: Int): String {
            var base = base
            if (!StringUtils.hasText(base)) {
                base = STRING
            }

            val builder = StringBuilder(len)

            for (i in 0 until len) {
                val index = nextInt(base.length)
                val c = base[index]
                builder.append(c)
            }

            return builder.toString()
        }

        fun nextStr(len: Int): String {
            return nextStr(STRING, len)
        }

        fun nextLetter(len: Int): String {
            return nextStr(LETTER, len)
        }

        fun nextNumber(len: Int): String {
            return nextStr(NUMBER, len)
        }

        fun nextHex(len: Int): String {
            val hex = nextStr(HEX, len)
            if (len > 1 && hex[0].code == 0) {
                return hex.substring(1) + nextHex(1)
            }
            return hex
        }
    }
}
