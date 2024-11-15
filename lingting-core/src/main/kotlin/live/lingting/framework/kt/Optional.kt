package live.lingting.framework.kt

import java.util.*

/**
 * @author lingting 2024/11/15 11:27
 */
fun <T> T?.optional(): Optional<T> = Optional.ofNullable(this) as Optional<T>
