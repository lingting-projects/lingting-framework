package live.lingting.framework.kt

import java.util.Enumeration

/**
 * @author lingting 2024/11/21 18:05
 */
fun <T : Any> Enumeration<T>.forEach(action: (T) -> Unit) {
    while (hasMoreElements()) {
        action(nextElement())
    }
}
