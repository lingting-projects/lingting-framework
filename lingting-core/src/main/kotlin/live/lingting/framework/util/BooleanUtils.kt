package live.lingting.framework.util

/**
 * @author lingting 2023-05-06 14:16
 */
class BooleanUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private val STR_TRUE = arrayOf("1", "true", "yes", "ok", "y", "t")

        private val STR_FALSE = arrayOf("0", "false", "no", "n", "f")


        fun isTrue(obj: Any): Boolean {
            if (obj is String) {
                return ArrayUtils.containsIgnoreCase(STR_TRUE, obj)
            }
            if (obj is Number) {
                return obj.toDouble() > 0
            }
            if (obj is Boolean) {
                return java.lang.Boolean.TRUE == obj
            }
            return false
        }


        fun isFalse(obj: Any): Boolean {
            if (obj is String) {
                return ArrayUtils.containsIgnoreCase(STR_FALSE, obj)
            }
            if (obj is Number) {
                return obj.toDouble() <= 0
            }
            if (obj is Boolean) {
                return java.lang.Boolean.FALSE == obj
            }
            return false
        }
    }
}
