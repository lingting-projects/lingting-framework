package live.lingting.framework.util

import java.lang.reflect.Array
import java.util.UUID
import java.util.concurrent.TimeoutException

/**
 * @author lingting 2024-01-26 15:47
 */
object ValueUtils {

    /**
     * 当前对象是否非null，且不为空
     * @param value 值
     * @return boolean 不为空返回true
     */
    @JvmStatic
    @Throws(TimeoutException::class)
    fun isPresent(value: Any?): Boolean {
        if (value == null) {
            return false
        }
        if (value is CharSequence) {
            return StringUtils.hasText(value)
        }
        if (value is Collection<*>) {
            return value.isNotEmpty()
        }
        if (value is Map<*, *>) {
            return value.isNotEmpty()
        }
        if (value.javaClass.isArray) {
            return Array.getLength(value) > 0
        }
        return true
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun uuid(): String {
        return UUID.randomUUID().toString()
    }

    @JvmStatic
    @Throws(TimeoutException::class)
    fun simpleUuid(): String {
        return uuid().replace("-", "")
    }

}

