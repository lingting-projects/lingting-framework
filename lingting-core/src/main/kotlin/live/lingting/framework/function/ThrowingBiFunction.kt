package live.lingting.framework.function

import live.lingting.framework.api.R

/**
 * @author lingting 2023/2/2 17:36
 */
fun interface ThrowingBiFunction<T, D, R> {

    fun apply(t: T, d: D): R
}
