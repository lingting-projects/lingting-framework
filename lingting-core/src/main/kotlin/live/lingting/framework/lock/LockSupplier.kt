package live.lingting.framework.lock

import live.lingting.framework.api.R

/**
 * @author lingting 2023-04-22 11:35
 */
interface LockSupplier<R> {

    fun get(): R
}
