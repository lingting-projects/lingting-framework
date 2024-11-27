package live.lingting.framework.util

import java.util.function.Supplier
import live.lingting.framework.util.ArrayUtils.containsIgnoreCase

object BooleanUtils {

    /**
     * @author lingting 2023-05-06 14:16
     */
    @JvmField
    val STR_TRUE = arrayOf("1", "true", "yes", "ok", "y", "t")

    @JvmField
    val STR_FALSE = arrayOf("0", "false", "no", "n", "f")

    @JvmStatic
    fun <T : Any?> T.isTrue(): Boolean {
        if (this == null) {
            return false
        }
        if (this is String) {
            return STR_TRUE.containsIgnoreCase(this)
        }
        if (this is Number) {
            return toDouble() > 0
        }
        if (this is Boolean) {
            return java.lang.Boolean.TRUE == this
        }
        return false
    }

    @JvmStatic
    fun <T : Any?> T.isFalse(): Boolean {
        if (this == null) {
            return false
        }
        if (this is String) {
            return STR_FALSE.containsIgnoreCase(this)
        }
        if (this is Number) {
            return toDouble() <= 0
        }
        if (this is Boolean) {
            return java.lang.Boolean.FALSE == this
        }
        return false
    }

    /**
     * @author lingting 2024/11/15 15:49
     */
    @JvmStatic
    fun <T : Boolean?> T.ifTrue(runnable: Runnable) {
        ifTrue(Supplier { runnable.run() })
    }

    @JvmStatic
    fun <T : Boolean?> T.ifFalse(runnable: Runnable) {
        ifFalse(Supplier { runnable.run() })
    }

    @JvmStatic
    fun <T : Boolean?, R : Any?> T.ifTrue(supplier: Supplier<R>): R? = if (isTrue()) supplier.get() else null

    @JvmStatic
    fun <T : Boolean?, R : Any?> T.ifFalse(supplier: Supplier<R>): R? = if (isFalse()) supplier.get() else null

}
