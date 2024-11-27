package live.lingting.framework.util

import java.util.Enumeration

/**
 * @author lingting 2024/11/21 18:05
 */
object EnumerationUtils {

    @JvmStatic
    fun <T : Any> Enumeration<T>.forEach(action: (T) -> Unit) {
        while (hasMoreElements()) {
            action(nextElement())
        }
    }

}
