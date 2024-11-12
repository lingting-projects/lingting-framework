package live.lingting.framework.util

import java.util.*
import java.util.function.BiPredicate

/**
 * @author lingting
 */
class ArrayUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val NOT_FOUNT: Int = -1

        /**
         * 数组是否为空
         *
         * @param obj 对象
         * @return true表示为空, 如果对象不为数组, 返回false
         */

        fun isEmpty(obj: Any?): Boolean {
            if (obj == null) {
                return true
            }
            if (!obj.javaClass.isArray()) {
                return false
            }

            val length = java.lang.reflect.Array.getLength(obj)
            return length < 1
        }

        fun <T> isEmpty(array: Array<T>?): Boolean {
            return array == null || array.size == 0
        }

        fun <T> indexOf(array: Array<T>, `val`: T): Int {
            return indexOf(array, `val`) { a: T, b: T -> Objects.equals(a, b) }
        }

        fun <T> indexOf(array: Array<T>, `val`: T, predicate: BiPredicate<T, T>): Int {
            if (!isEmpty<T>(array)) {
                for (i in array.indices) {
                    val t = array[i]
                    if (predicate.test(t, `val`)) {
                        return i
                    }
                }
            }
            return NOT_FOUNT
        }

        fun <T> contains(array: Array<T>, `val`: T): Boolean {
            return indexOf(array, `val`) > NOT_FOUNT
        }

        fun containsIgnoreCase(array: Array<String>, `val`: String): Boolean {
            return indexOf(array, `val`) { s: String?, t: String? ->
                if (s == t) {
                    return@indexOf true
                }
                if (s == null || t == null) {
                    return@indexOf false
                }
                s.equals(t, ignoreCase = true)
            } > NOT_FOUNT
        }

        fun <T> isEquals(array1: Array<T>, array2: Array<T>): Boolean {
            val empty1 = isEmpty(array1)
            val empty2 = isEmpty(array2)

            if (empty1 || empty2) {
                return empty1 && empty2
            }

            if (array1.size != array2.size) {
                return false
            }

            for (i in array1.indices) {
                if (array1[i] != array2[i]) {
                    return false
                }
            }
            return true
        }

        fun <T> isEquals(array1: Array<T>, array1Pos: Int, array2: Array<T>, array2Pos: Int, len: Int): Boolean {
            val empty1 = isEmpty(array1)
            val empty2 = isEmpty(array2)

            if (empty1 || empty2) {
                return empty1 && empty2
            }

            for (i in 0 until len) {
                val i1: Int = array1Pos + i
                val i2: Int = array2Pos + i

                // 是否越界
                val o1 = array1.size <= i1
                val o2 = array2.size <= i2

                if (o1 || o2) {
                    // 如果同时越界, 则相等, 否则不等
                    return o1 && o2
                }

                val t1 = array1[i1]
                val t2 = array2[i2]
                if (t1 != t2) {
                    return false
                }
            }
            return true
        }

        fun <T> sub(array: Array<T>?, start: Int): Array<T>? {
            if (array == null) {
                return null
            }
            return sub(array, start, array.size)
        }

        /**
         * 截取数组
         *
         * @param array 数组
         * @param start 左闭
         * @param end   右开
         */
        fun <T> sub(array: Array<T>?, start: Int, end: Int): Array<T>? {
            if (array == null) {
                return null
            }
            val type: Class<*> = array.javaClass.getComponentType()

            if (array.size < 1) {
                return java.lang.reflect.Array.newInstance(type, 0) as Array<T>
            }

            val fromIndex: Int = max(0, start)
            val toIndex: Int = min(array.size + 1, end)
            val result = java.lang.reflect.Array.newInstance(type, toIndex - fromIndex) as Array<T>

            for (i in fromIndex until toIndex) {
                val t = array[i]
                result[i - fromIndex] = t
            }

            return result
        }
    }
}
