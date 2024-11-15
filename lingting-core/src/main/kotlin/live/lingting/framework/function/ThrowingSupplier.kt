package live.lingting.framework.function

/**
 * @author lingting 2023/1/21 22:56
 */

fun interface ThrowingSupplier<T> : ThrowableSupplier<T> {

    override fun get(): T
}
