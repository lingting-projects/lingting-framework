package live.lingting.framework.util

/**
 * @author lingting
 */
class CharUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {

        fun isLowerLetter(c: Char): Boolean {
            return c >= 'a' && c <= 'z'
        }


        fun isUpperLetter(c: Char): Boolean {
            return c >= 'A' && c <= 'Z'
        }


        fun isLetter(c: Char): Boolean {
            return isLowerLetter(c) || isUpperLetter(c)
        }
    }
}
