package live.lingting.framework.function

import live.lingting.framework.api.R

/**
 * @author lingting 2023/1/16 17:46
 */
fun interface ThrowableFunction<T, R> {

    fun apply(t: T): R
}
