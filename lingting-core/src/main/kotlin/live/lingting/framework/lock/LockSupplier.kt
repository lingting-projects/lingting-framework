package live.lingting.framework.lock

/**
 * @author lingting 2023-04-22 11:35
 */
fun interface LockSupplier<R> {

    fun get(): R

}
