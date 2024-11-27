package live.lingting.framework.util

import java.lang.reflect.Array.getLength
import java.lang.reflect.Array.newInstance
import java.util.Objects
import java.util.function.BiPredicate
import kotlin.math.max
import kotlin.math.min

/**
 * @author lingting
 */
@Suppress("UNCHECKED_CAST")
object ArrayUtils {
    const val NOT_FOUNT: Int = -1

    /**
     * 数组是否为空
     * @param obj 对象
     * @return true表示为空, 如果对象不为数组, 返回false
     */
    @JvmStatic
    fun <T : Any> T?.isEmpty(): Boolean {
        if (this == null) {
            return true
        }
        if (!javaClass.isArray) {
            return false
        }

        val length = getLength(this)
        return length < 1
    }

    @JvmStatic
    fun <T : Any> Array<T>?.isEmpty(): Boolean {
        return isNullOrEmpty()
    }

    @JvmStatic
    fun <T : Any> Array<T>.indexOf(`val`: T): Int {
        return indexOf(`val`) { a, b -> Objects.equals(a, b) }
    }

    @JvmStatic
    fun <T : Any> Array<T>.indexOf(`val`: T, predicate: BiPredicate<T, T>): Int {
        if (!isEmpty<T>()) {
            for (i in indices) {
                val t = this[i]
                if (predicate.test(t, `val`)) {
                    return i
                }
            }
        }
        return NOT_FOUNT
    }

    @JvmStatic
    fun <T : Any> Array<T>.contains(`val`: T): Boolean {
        return indexOf(`val`) > NOT_FOUNT
    }

    @JvmStatic
    fun Array<String>.containsIgnoreCase(`val`: String): Boolean {
        return indexOf(`val`) { s, t ->
            if (s == t) {
                return@indexOf true
            }
            s.equals(t, ignoreCase = true)
        } > NOT_FOUNT
    }

    @JvmStatic
    fun <T : Any> Array<T>.isEquals(array2: Array<T>): Boolean {
        return isEquals(0, array2, 0, max(size, array2.size))
    }

    @JvmStatic
    fun <T : Any> Array<T>.isEquals(array1Pos: Int, array2: Array<T>, array2Pos: Int, len: Int): Boolean {
        val empty1 = isEmpty()
        val empty2 = array2.isEmpty()

        if (empty1 || empty2) {
            return empty1 && empty2
        }

        for (i in 0 until len) {
            val i1: Int = array1Pos + i
            val i2: Int = array2Pos + i

            // 是否越界
            val o1 = size <= i1
            val o2 = array2.size <= i2

            if (o1 || o2) {
                // 如果同时越界, 则相等, 否则不等
                return o1 && o2
            }

            val t1 = this[i1]
            val t2 = array2[i2]
            if (t1 != t2) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun <T : Any> Array<T>?.sub(start: Int): Array<T>? {
        if (this == null) {
            return null
        }
        return sub(start, size)
    }

    /**
     * 截取数组
     * @param array 数组
     * @param start 左闭
     * @param end   右开
     */
    @JvmStatic
    fun <T : Any> Array<T>.sub(start: Int, end: Int): Array<T> {
        val type: Class<*> = javaClass.componentType

        if (isEmpty()) {
            return newInstance(type, 0) as Array<T>
        }

        val fromIndex: Int = max(0, start)
        val toIndex: Int = min(size + 1, end)
        val result = newInstance(type, toIndex - fromIndex) as Array<T>

        for (i in fromIndex until toIndex) {
            val t = this[i]
            result[i - fromIndex] = t
        }

        return result
    }
}
