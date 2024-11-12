package live.lingting.framework.util

import java.math.BigDecimal
import java.math.BigInteger

/**
 * 位运算
 *
 * @author lingting 2023-11-24 11:48
 */
class NumberUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val DECIMAL_TWO: BigDecimal = BigDecimal("2")


        val INTEGER_TWO: BigInteger = DECIMAL_TWO.toBigInteger()

        /**
         * 指定数字是否为整数
         */

        fun isInteger(v: Number): Boolean {
            if (v is Double || v is Float) {
                return false
            }

            if (v is BigDecimal) {
                return v.scale() <= 0 || v.stripTrailingZeros().scale() <= 0
            }

            return true
        }

        /**
         * 是否为大数
         */

        fun isBig(v: Number?): Boolean {
            return v is BigDecimal || v is BigInteger
        }

        /**
         * 指定数字是否为 2 的整数次幂
         *
         *
         * 对于大数, 仅支持正整数
         *
         *
         * @return true 表示是2的整数次幂
         */

        fun isPower2(v: Number): Boolean {
            if (isBig(v)) {
                // 非整数大数为false
                if (!isInteger(v)) {
                    return false
                }
                val bi = if (v is BigDecimal) {
                    v.toBigInteger()
                } else {
                    v as BigInteger
                }

                /*
 * 正数的符号位是0
 *
 * 正数的补码是原码
 *
 * 所以如果 补码形式与符号位不同的位数为1, 即 原码中与符号位不同的位数为1, 比 原码中与 0 不同的位数只有一个, 表示该数为2的整数次幂
 */
                return bi.bitCount() == 1
            }

            if (isInteger(v)) {
                val l = v.toLong()
                val b = l and (l - 1)
                return b == 0L
            }

            // 带小数的转decimal
            val d = v.toDouble()
            val ds = d.toString()
            val decimal = BigDecimal(ds)

            // 如果是整数则重新算, 否则为false
            if (isInteger(decimal)) {
                return isPower2(decimal.toLong())
            }
            return false
        }

        /**
         * 指定数字是否为偶数
         */

        fun isEven(v: Number): Boolean {
            if (v is BigDecimal) {
                val decimals = v.divideAndRemainder(DECIMAL_TWO)
                // 余数为0表示是偶数
                return decimals[1].compareTo(BigDecimal.ZERO) == 0
            }
            if (v is BigInteger) {
                val integers = v.divideAndRemainder(INTEGER_TWO)
                // 余数为0表示是偶数
                return integers[1].compareTo(BigInteger.ZERO) == 0
            }
            if (isPower2(v)) {
                return true
            }
            if (isInteger(v)) {
                return v.toLong() % 2 == 0L
            }
            return v.toDouble() % 2 == 0.0
        }

        /**
         * 计算数字的整数部分二进制位数
         */

        fun bitLength(v: Number): Int {
            // 大数处理
            if (isBig(v)) {
                val bi = if (v is BigDecimal) {
                    v.toBigInteger()
                } else {
                    v as BigInteger
                }
                return bi.bitLength()
            } else {
                val l = v.toLong()
                val binaryString = java.lang.Long.toBinaryString(l)
                return binaryString.length
            }
        }

        /**
         * 获取指定数字的下一个2的整数次幂值
         */

        fun nextPower2(v: Number): BigInteger {
            if (isPower2(v)) {
                if (v is BigDecimal) {
                    return v.toBigInteger()
                } else if (v is BigInteger) {
                    return v
                }

                return BigInteger(v.toLong().toString())
            }

            val length = bitLength(v)
            // 拼接下一个2的整数次幂值的二进制字符串
            val binary: String = StringUtils.append("1", length, "0")
            // 转数字
            return BigInteger(binary, 2)
        }

        fun toNumber(`val`: Any?): BigDecimal? {
            if (`val` == null) {
                return null
            }
            val string = `val`.toString()
            if (!StringUtils.hasText(string)) {
                return null
            }
            return try {
                BigDecimal(string.trim { it <= ' ' })
            } catch (e: Exception) {
                null
            }
        }
    }
}
