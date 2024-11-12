package live.lingting.framework.function

/**
 * @author lingting 2023/1/21 22:56
 */
fun interface ThrowableSupplier<T> {

    fun get(): T
}
