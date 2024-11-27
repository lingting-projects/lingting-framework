package live.lingting.framework.util

import java.util.Optional

/**
 * @author lingting 2024/11/15 11:27
 */
@Suppress("UNCHECKED_CAST")
object OptionalUtils {

    @JvmStatic
    fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this) as Optional<T>

}
