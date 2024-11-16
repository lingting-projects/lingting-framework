package live.lingting.framework.util

/**
 * @author lingting
 */
object CharUtils {
    @JvmStatic
    fun isLowerLetter(c: Char): Boolean {
        return c >= 'a' && c <= 'z'
    }

    @JvmStatic
    fun isUpperLetter(c: Char): Boolean {
        return c >= 'A' && c <= 'Z'
    }

    @JvmStatic
    fun isLetter(c: Char): Boolean {
        return isLowerLetter(c) || isUpperLetter(c)
    }
}

