package live.lingting.framework.function

/**
 * @author lingting 2023/1/21 22:56
 */
fun interface ThrowableSupplier<T> {

    @Throws(Throwable::class)
    fun get(): T

}
