package live.lingting.framework.function

import live.lingting.framework.api.R

/**
 * @author lingting 2023/2/2 17:36
 */

interface ThrowingFunction<T, R> : ThrowableFunction<T, R> {

    override fun apply(t: T): R
}
