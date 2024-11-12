package live.lingting.framework.function

/**
 * @author lingting 2023/1/21 22:56
 */

interface ThrowingSupplier<T> : ThrowableSupplier<T> {

    override fun get(): T
}
